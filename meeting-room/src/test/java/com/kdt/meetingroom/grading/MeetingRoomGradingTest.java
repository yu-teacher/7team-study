package com.kdt.meetingroom.grading;

import com.kdt.meetingroom.api.MeetingRoomApi;
import com.kdt.meetingroom.api.MeetingRoomController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("회의실 예약 채점")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
class MeetingRoomGradingTest {
    private static final String CHULSOO = "철수";
    private static final String YOUNGHEE = "영희";

    private MeetingRoomApi rooms;

    @BeforeEach
    void setUp() {
        rooms = new MeetingRoomController();
    }

    @Nested
    @Order(1)
    @DisplayName("1단계: 시간대")
    class Step1 {

        @ParameterizedTest(name = "1-1. {0}~{1}은 {2}분")
        @CsvSource({
                "10:00, 11:00, 60",
                "10:00, 10:30, 30",
                "09:00, 18:00, 540",
                "13:30, 15:00, 90"
        })
        void minutes(String start, String end, int expected) {
            assertThat(rooms.minutes(start, end)).isEqualTo(expected);
        }

        @ParameterizedTest(name = "1-2. 형식이 틀린 시각 \"{0}\"은 IllegalArgumentException")
        @ValueSource(strings = {"", "10", "9:00", "10:0", "10-00", "1000", "ab:cd", "25:00", "10:60", " 10:00"})
        void rejectsBadFormat(String start) {
            assertThatThrownBy(() -> rooms.minutes(start, "17:00"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @ParameterizedTest(name = "1-3. {0}~{1}은 IllegalArgumentException ({2})")
        @CsvSource({
                "10:15, 11:00, 30분 단위가 아님",
                "10:00, 10:45, 30분 단위가 아님",
                "08:30, 10:00, 09:00 전",
                "17:00, 18:30, 18:00 뒤",
                "11:00, 10:00, 시작이 끝보다 늦음",
                "10:00, 10:00, 시작과 끝이 같음"
        })
        void rejectsBadSlot(String start, String end, String reason) {
            assertThatThrownBy(() -> rooms.minutes(start, end))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @ParameterizedTest(name = "1-4. {0}~{1}과 {2}~{3}은 겹치는가? {4}")
        @CsvSource({
                "10:00, 11:00, 10:30, 11:30, true",
                "10:00, 12:00, 10:30, 11:00, true",
                "10:00, 11:00, 10:00, 11:00, true",
                "10:00, 11:00, 11:00, 12:00, false",
                "11:00, 12:00, 10:00, 11:00, false",
                "10:00, 11:00, 14:00, 15:00, false"
        })
        void overlaps(String s1, String e1, String s2, String e2, boolean expected) {
            assertThat(rooms.overlaps(s1, e1, s2, e2)).isEqualTo(expected);
        }

        @Test
        @DisplayName("1-5. 겹침을 물을 때도 시각 규칙을 지켜야 한다")
        void overlapsValidates() {
            assertThatThrownBy(() -> rooms.overlaps("10:00", "11:00", "10:15", "11:00"))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @Order(2)
    @DisplayName("2단계: 예약")
    class Step2 {

        @Test
        @DisplayName("2-1. 예약 번호는 1부터 하나씩 늘어난다")
        void idsIncrease() {
            assertThat(rooms.reserve("A", CHULSOO, "10:00", "11:00")).isEqualTo(1L);
            assertThat(rooms.reserve("B", YOUNGHEE, "10:00", "11:00")).isEqualTo(2L);
        }

        @Test
        @DisplayName("2-2. 회의실 일정은 시작 시각 순서로 \"시작-끝 예약자\"를 보여 준다")
        void schedule() {
            rooms.reserve("A", CHULSOO, "14:00", "15:00");
            rooms.reserve("A", YOUNGHEE, "10:00", "10:30");
            rooms.reserve("B", CHULSOO, "11:00", "12:00");

            assertThat(rooms.schedule("A")).containsExactly("10:00-10:30 영희", "14:00-15:00 철수");
            assertThat(rooms.schedule("B")).containsExactly("11:00-12:00 철수");
            assertThat(rooms.schedule("C")).isEmpty();
        }

        @Test
        @DisplayName("2-3. 같은 회의실에서 겹치면 IllegalStateException, 일정 그대로")
        void rejectsOverlap() {
            rooms.reserve("A", CHULSOO, "10:00", "11:00");

            assertThatThrownBy(() -> rooms.reserve("A", YOUNGHEE, "10:30", "11:30"))
                    .isInstanceOf(IllegalStateException.class);
            assertThat(rooms.schedule("A")).containsExactly("10:00-11:00 철수");
        }

        @Test
        @DisplayName("2-4. 끝과 시작이 맞닿으면 예약할 수 있다")
        void touchingIsFine() {
            rooms.reserve("A", CHULSOO, "10:00", "11:00");

            rooms.reserve("A", YOUNGHEE, "11:00", "12:00");

            assertThat(rooms.schedule("A")).containsExactly("10:00-11:00 철수", "11:00-12:00 영희");
        }

        @Test
        @DisplayName("2-5. 다른 회의실이면 같은 시간도 예약할 수 있다")
        void otherRoomIsFine() {
            rooms.reserve("A", CHULSOO, "10:00", "11:00");

            assertThat(rooms.reserve("B", YOUNGHEE, "10:00", "11:00")).isEqualTo(2L);
        }

        @ParameterizedTest(name = "2-6. 회의실 \"{0}\"은 IllegalArgumentException")
        @ValueSource(strings = {"", "D", "a", "A1"})
        void rejectsUnknownRoom(String room) {
            assertThatThrownBy(() -> rooms.reserve(room, CHULSOO, "10:00", "11:00"))
                    .isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> rooms.schedule(room))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @ParameterizedTest(name = "2-7. 예약자 \"{0}\"은 IllegalArgumentException")
        @ValueSource(strings = {"", "  "})
        void rejectsBlankMember(String member) {
            assertThatThrownBy(() -> rooms.reserve("A", member, "10:00", "11:00"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("2-8. 실패한 예약은 번호를 쓰지 않는다")
        void failedDoesNotUseId() {
            rooms.reserve("A", CHULSOO, "10:00", "11:00");
            assertThatThrownBy(() -> rooms.reserve("A", YOUNGHEE, "10:00", "11:00"))
                    .isInstanceOf(IllegalStateException.class);
            assertThatThrownBy(() -> rooms.reserve("A", YOUNGHEE, "10:15", "11:00"))
                    .isInstanceOf(IllegalArgumentException.class);

            assertThat(rooms.reserve("A", YOUNGHEE, "11:00", "12:00")).isEqualTo(2L);
        }
    }

    @Nested
    @Order(3)
    @DisplayName("3단계: 현재 시각과 취소")
    class Step3 {

        @Test
        @DisplayName("3-1. 처음 현재 시각은 09:00이라 09:00부터 예약할 수 있다")
        void startsAtNine() {
            assertThat(rooms.reserve("A", CHULSOO, "09:00", "10:00")).isEqualTo(1L);
        }

        @Test
        @DisplayName("3-2. 현재 시각보다 이른 시작은 IllegalStateException")
        void rejectsPast() {
            rooms.now("10:15");

            assertThatThrownBy(() -> rooms.reserve("A", CHULSOO, "10:00", "11:00"))
                    .isInstanceOf(IllegalStateException.class);
            assertThat(rooms.schedule("A")).isEmpty();
        }

        @Test
        @DisplayName("3-3. 현재 시각과 시작이 같으면 예약할 수 있다")
        void startingNowIsFine() {
            rooms.now("10:00");

            assertThat(rooms.reserve("A", CHULSOO, "10:00", "11:00")).isEqualTo(1L);
        }

        @ParameterizedTest(name = "3-4. 현재 시각 \"{0}\"은 IllegalArgumentException")
        @ValueSource(strings = {"", "9:00", "24:00", "10:60", "abc"})
        void rejectsBadNow(String time) {
            assertThatThrownBy(() -> rooms.now(time))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("3-5. 현재 시각은 거꾸로 갈 수 없다 (IllegalArgumentException)")
        void timeDoesNotGoBack() {
            rooms.now("12:00");

            assertThatThrownBy(() -> rooms.now("11:00"))
                    .isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> rooms.reserve("A", CHULSOO, "11:30", "12:00"))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("3-6. 예약한 사람이 취소하면 일정에서 빠지고, 그 시간을 다시 예약할 수 있다")
        void cancel() {
            long id = rooms.reserve("A", CHULSOO, "10:00", "11:00");

            rooms.cancel(id, CHULSOO);

            assertThat(rooms.schedule("A")).isEmpty();
            assertThat(rooms.reserve("A", YOUNGHEE, "10:00", "11:00")).isEqualTo(2L);
        }

        @Test
        @DisplayName("3-7. 다른 사람이 취소하면 IllegalStateException, 일정 그대로")
        void othersCannotCancel() {
            long id = rooms.reserve("A", CHULSOO, "10:00", "11:00");

            assertThatThrownBy(() -> rooms.cancel(id, YOUNGHEE))
                    .isInstanceOf(IllegalStateException.class);
            assertThat(rooms.schedule("A")).containsExactly("10:00-11:00 철수");
        }

        @Test
        @DisplayName("3-8. 시작했거나 지난 예약은 취소할 수 없다 (IllegalStateException)")
        void cannotCancelStarted() {
            long id = rooms.reserve("A", CHULSOO, "10:00", "11:00");
            rooms.now("10:00");

            assertThatThrownBy(() -> rooms.cancel(id, CHULSOO))
                    .isInstanceOf(IllegalStateException.class);
            assertThat(rooms.schedule("A")).containsExactly("10:00-11:00 철수");
        }

        @Test
        @DisplayName("3-9. 시작 전이면 취소할 수 있다")
        void canCancelBeforeStart() {
            long id = rooms.reserve("A", CHULSOO, "10:00", "11:00");
            rooms.now("09:59");

            rooms.cancel(id, CHULSOO);

            assertThat(rooms.schedule("A")).isEmpty();
        }

        @Test
        @DisplayName("3-10. 없거나 이미 취소한 예약을 취소하면 NoSuchElementException")
        void cancelMissing() {
            long id = rooms.reserve("A", CHULSOO, "10:00", "11:00");
            rooms.cancel(id, CHULSOO);

            assertThatThrownBy(() -> rooms.cancel(id, CHULSOO))
                    .isInstanceOf(NoSuchElementException.class);
            assertThatThrownBy(() -> rooms.cancel(99, CHULSOO))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }

    @Nested
    @Order(4)
    @DisplayName("4단계: 한 사람의 하루 한도")
    class Step4 {

        @Test
        @DisplayName("4-1. 예약한 시간을 분으로 더한다 (취소한 예약은 빼고, 처음엔 0)")
        void usedMinutes() {
            assertThat(rooms.usedMinutes(CHULSOO)).isZero();

            rooms.reserve("A", CHULSOO, "10:00", "11:00");
            long id = rooms.reserve("B", CHULSOO, "13:00", "13:30");
            assertThat(rooms.usedMinutes(CHULSOO)).isEqualTo(90);

            rooms.cancel(id, CHULSOO);
            assertThat(rooms.usedMinutes(CHULSOO)).isEqualTo(60);
        }

        @Test
        @DisplayName("4-2. 하루 합계가 2시간을 넘으면 IllegalStateException")
        void dailyLimit() {
            rooms.reserve("A", CHULSOO, "10:00", "11:00");
            rooms.reserve("B", CHULSOO, "13:00", "13:30");

            assertThatThrownBy(() -> rooms.reserve("C", CHULSOO, "15:00", "16:00"))
                    .isInstanceOf(IllegalStateException.class);
            assertThat(rooms.usedMinutes(CHULSOO)).isEqualTo(90);
            assertThat(rooms.schedule("C")).isEmpty();
        }

        @Test
        @DisplayName("4-3. 딱 2시간까지는 예약할 수 있다")
        void exactlyTwoHours() {
            rooms.reserve("A", CHULSOO, "10:00", "11:00");

            rooms.reserve("B", CHULSOO, "13:00", "14:00");

            assertThat(rooms.usedMinutes(CHULSOO)).isEqualTo(120);
        }

        @Test
        @DisplayName("4-4. 한 번에 2시간을 넘게 잡으면 IllegalStateException")
        void singleTooLong() {
            assertThatThrownBy(() -> rooms.reserve("A", CHULSOO, "10:00", "12:30"))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("4-5. 취소하면 한도가 다시 생긴다")
        void cancelFreesLimit() {
            long id = rooms.reserve("A", CHULSOO, "10:00", "12:00");
            rooms.cancel(id, CHULSOO);

            assertThat(rooms.reserve("B", CHULSOO, "14:00", "16:00")).isEqualTo(2L);
        }

        @Test
        @DisplayName("4-6. 한도는 사람마다 따로 센다")
        void limitPerMember() {
            rooms.reserve("A", CHULSOO, "10:00", "12:00");

            assertThat(rooms.reserve("B", YOUNGHEE, "13:00", "15:00")).isEqualTo(2L);
            assertThat(rooms.usedMinutes(YOUNGHEE)).isEqualTo(120);
        }

        @Test
        @DisplayName("4-7. 한 사람이 같은 시간에 두 회의실을 잡으면 IllegalStateException")
        void oneMemberOnePlace() {
            rooms.reserve("A", CHULSOO, "10:00", "11:00");

            assertThatThrownBy(() -> rooms.reserve("B", CHULSOO, "10:30", "11:00"))
                    .isInstanceOf(IllegalStateException.class);
            assertThat(rooms.schedule("B")).isEmpty();
        }

        @ParameterizedTest(name = "4-8. 예약자 \"{0}\"의 한도를 물으면 IllegalArgumentException")
        @ValueSource(strings = {"", "  "})
        void rejectsBlankMember(String member) {
            assertThatThrownBy(() -> rooms.usedMinutes(member))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
