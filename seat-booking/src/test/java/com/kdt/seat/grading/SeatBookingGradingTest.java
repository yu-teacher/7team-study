package com.kdt.seat.grading;

import com.kdt.seat.api.SeatBookingApi;
import com.kdt.seat.api.SeatBookingController;
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

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("공연 좌석 예매 채점")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
class SeatBookingGradingTest {
    private static final String CHULSOO = "철수";
    private static final String YOUNGHEE = "영희";

    private SeatBookingApi concert;

    @BeforeEach
    void setUp() {
        concert = new SeatBookingController();
    }

    @Nested
    @Order(1)
    @DisplayName("1단계: 좌석과 등급")
    class Step1 {

        @ParameterizedTest(name = "1-1. {0}은 {1}석, {2}원")
        @CsvSource({
                "A-1, VIP, 150000",
                "B-10, VIP, 150000",
                "C-1, R, 100000",
                "F-7, R, 100000",
                "G-1, S, 70000",
                "J-10, S, 70000"
        })
        void gradeAndPrice(String seat, String grade, int price) {
            assertThat(concert.grade(seat)).isEqualTo(grade);
            assertThat(concert.price(seat)).isEqualTo(price);
        }

        @ParameterizedTest(name = "1-2. 좌석 \"{0}\"은 IllegalArgumentException")
        @ValueSource(strings = {"", "A1", "A-0", "A-11", "K-1", "a-1", "A-01", "AA-1", "A--1", " A-1", "A-1 "})
        void rejectsBadSeat(String seat) {
            assertThatThrownBy(() -> concert.grade(seat)).isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> concert.price(seat)).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @Order(2)
    @DisplayName("2단계: 예매")
    class Step2 {

        @Test
        @DisplayName("2-1. 예매 번호는 1부터 하나씩 늘어나고, 예매한 좌석은 팔린 좌석이 된다")
        void book() {
            assertThat(concert.book(CHULSOO, List.of("A-1", "A-2"))).isEqualTo(1L);
            assertThat(concert.book(YOUNGHEE, List.of("C-5"))).isEqualTo(2L);

            assertThat(concert.isSold("A-1")).isTrue();
            assertThat(concert.isSold("C-5")).isTrue();
            assertThat(concert.isSold("A-3")).isFalse();
        }

        @Test
        @DisplayName("2-2. 합계는 좌석 가격을 더한 값이다")
        void total() {
            long id = concert.book(CHULSOO, List.of("A-1", "C-1", "G-1", "J-10"));

            assertThat(concert.total(id)).isEqualTo(390_000);
        }

        @Test
        @DisplayName("2-3. 예매한 좌석은 열, 번호 순서로 보여 준다 (A-2가 A-10보다 앞)")
        void seatsInOrder() {
            long id = concert.book(CHULSOO, List.of("B-1", "A-10", "A-2"));

            assertThat(concert.seats(id)).containsExactly("A-2", "A-10", "B-1");
        }

        @Test
        @DisplayName("2-4. 고른 좌석 중 하나라도 팔렸으면 IllegalStateException, 아무 좌석도 팔리지 않는다")
        void allOrNothing() {
            concert.book(CHULSOO, List.of("A-3"));

            assertThatThrownBy(() -> concert.book(YOUNGHEE, List.of("A-1", "A-2", "A-3", "A-4")))
                    .isInstanceOf(IllegalStateException.class);
            assertThat(concert.isSold("A-1")).isFalse();
            assertThat(concert.isSold("A-2")).isFalse();
            assertThat(concert.isSold("A-4")).isFalse();
        }

        @Test
        @DisplayName("2-5. 한 번에 1~4석만 예매할 수 있다 (IllegalArgumentException)")
        void seatCount() {
            assertThatThrownBy(() -> concert.book(CHULSOO, List.of()))
                    .isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> concert.book(CHULSOO, List.of("A-1", "A-2", "A-3", "A-4", "A-5")))
                    .isInstanceOf(IllegalArgumentException.class);
            assertThat(concert.isSold("A-1")).isFalse();
        }

        @Test
        @DisplayName("2-6. 같은 좌석을 두 번 고르면 IllegalArgumentException")
        void duplicateSeat() {
            assertThatThrownBy(() -> concert.book(CHULSOO, List.of("A-1", "A-1")))
                    .isInstanceOf(IllegalArgumentException.class);
            assertThat(concert.isSold("A-1")).isFalse();
        }

        @Test
        @DisplayName("2-7. 잘못된 좌석이 섞여 있으면 IllegalArgumentException, 아무 좌석도 팔리지 않는다")
        void badSeatInList() {
            assertThatThrownBy(() -> concert.book(CHULSOO, List.of("A-1", "K-1")))
                    .isInstanceOf(IllegalArgumentException.class);
            assertThat(concert.isSold("A-1")).isFalse();
        }

        @ParameterizedTest(name = "2-8. 예매자 \"{0}\"은 IllegalArgumentException")
        @ValueSource(strings = {"", "  "})
        void blankCustomer(String customer) {
            assertThatThrownBy(() -> concert.book(customer, List.of("A-1")))
                    .isInstanceOf(IllegalArgumentException.class);
            assertThat(concert.isSold("A-1")).isFalse();
        }

        @Test
        @DisplayName("2-9. 실패한 예매는 번호를 쓰지 않는다")
        void failedDoesNotUseId() {
            concert.book(CHULSOO, List.of("A-1"));
            assertThatThrownBy(() -> concert.book(YOUNGHEE, List.of("A-1")))
                    .isInstanceOf(IllegalStateException.class);

            assertThat(concert.book(YOUNGHEE, List.of("A-2"))).isEqualTo(2L);
        }

        @Test
        @DisplayName("2-10. 없는 예매는 NoSuchElementException, 잘못된 좌석을 물으면 IllegalArgumentException")
        void missing() {
            assertThatThrownBy(() -> concert.total(9)).isInstanceOf(NoSuchElementException.class);
            assertThatThrownBy(() -> concert.seats(9)).isInstanceOf(NoSuchElementException.class);
            assertThatThrownBy(() -> concert.isSold("Z-1")).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @Order(3)
    @DisplayName("3단계: 취소와 환불")
    class Step3 {

        @ParameterizedTest(name = "3-1. 공연 {0}일 전에 취소하면 {1}원 환불")
        @CsvSource({
                "30, 250000",
                "3, 250000",
                "2, 125000",
                "1, 125000"
        })
        void refund(int daysLeft, int refund) {
            long id = concert.book(CHULSOO, List.of("A-1", "C-1"));

            assertThat(concert.cancel(id, CHULSOO, daysLeft)).isEqualTo(refund);
        }

        @Test
        @DisplayName("3-2. 취소한 좌석은 다시 예매할 수 있고, 취소한 예매는 없는 예매가 된다")
        void cancelFreesSeats() {
            long id = concert.book(CHULSOO, List.of("A-1", "A-2"));

            concert.cancel(id, CHULSOO, 5);

            assertThat(concert.isSold("A-1")).isFalse();
            assertThat(concert.isSold("A-2")).isFalse();
            assertThatThrownBy(() -> concert.total(id)).isInstanceOf(NoSuchElementException.class);
            assertThat(concert.book(YOUNGHEE, List.of("A-1"))).isEqualTo(2L);
        }

        @Test
        @DisplayName("3-3. 공연 당일(0일 전)에는 취소할 수 없다 (IllegalStateException), 예매 그대로")
        void sameDay() {
            long id = concert.book(CHULSOO, List.of("A-1"));

            assertThatThrownBy(() -> concert.cancel(id, CHULSOO, 0))
                    .isInstanceOf(IllegalStateException.class);
            assertThat(concert.isSold("A-1")).isTrue();
            assertThat(concert.total(id)).isEqualTo(150_000);
        }

        @Test
        @DisplayName("3-4. 남은 날짜가 음수면 IllegalArgumentException")
        void negativeDays() {
            long id = concert.book(CHULSOO, List.of("A-1"));

            assertThatThrownBy(() -> concert.cancel(id, CHULSOO, -1))
                    .isInstanceOf(IllegalArgumentException.class);
            assertThat(concert.isSold("A-1")).isTrue();
        }

        @Test
        @DisplayName("3-5. 다른 사람이 취소하면 IllegalStateException, 예매 그대로")
        void othersCannotCancel() {
            long id = concert.book(CHULSOO, List.of("A-1"));

            assertThatThrownBy(() -> concert.cancel(id, YOUNGHEE, 5))
                    .isInstanceOf(IllegalStateException.class);
            assertThat(concert.isSold("A-1")).isTrue();
        }

        @Test
        @DisplayName("3-6. 없거나 이미 취소한 예매를 취소하면 NoSuchElementException")
        void cancelMissing() {
            long id = concert.book(CHULSOO, List.of("A-1"));
            concert.cancel(id, CHULSOO, 5);

            assertThatThrownBy(() -> concert.cancel(id, CHULSOO, 5)).isInstanceOf(NoSuchElementException.class);
            assertThatThrownBy(() -> concert.cancel(9, CHULSOO, 5)).isInstanceOf(NoSuchElementException.class);
        }
    }

    @Nested
    @Order(4)
    @DisplayName("4단계: 1인 한도와 부분 취소")
    class Step4 {

        @Test
        @DisplayName("4-1. 한 사람은 여러 번 나눠도 모두 합해 4석까지 예매할 수 있다")
        void limitAcrossBookings() {
            concert.book(CHULSOO, List.of("A-1", "A-2", "A-3"));

            assertThatThrownBy(() -> concert.book(CHULSOO, List.of("B-1", "B-2")))
                    .isInstanceOf(IllegalStateException.class);
            assertThat(concert.isSold("B-1")).isFalse();
            assertThat(concert.book(CHULSOO, List.of("B-1"))).isEqualTo(2L);
        }

        @Test
        @DisplayName("4-2. 한도는 사람마다 따로 세고, 취소하면 다시 생긴다")
        void limitPerCustomerAndCancel() {
            long id = concert.book(CHULSOO, List.of("A-1", "A-2", "A-3", "A-4"));
            assertThat(concert.book(YOUNGHEE, List.of("B-1", "B-2", "B-3", "B-4"))).isEqualTo(2L);

            concert.cancel(id, CHULSOO, 5);

            assertThat(concert.book(CHULSOO, List.of("C-1", "C-2", "C-3", "C-4"))).isEqualTo(3L);
        }

        @Test
        @DisplayName("4-3. 좌석 하나만 취소하면 그 좌석 가격 기준으로 환불하고, 합계와 좌석이 줄어든다")
        void cancelSeat() {
            long id = concert.book(CHULSOO, List.of("A-1", "C-1", "G-1"));

            assertThat(concert.cancelSeat(id, CHULSOO, "C-1", 5)).isEqualTo(100_000);

            assertThat(concert.seats(id)).containsExactly("A-1", "G-1");
            assertThat(concert.total(id)).isEqualTo(220_000);
            assertThat(concert.isSold("C-1")).isFalse();
        }

        @Test
        @DisplayName("4-4. 부분 취소도 같은 환불 규칙을 따른다")
        void cancelSeatRefund() {
            long id = concert.book(CHULSOO, List.of("A-1", "G-1"));

            assertThat(concert.cancelSeat(id, CHULSOO, "G-1", 2)).isEqualTo(35_000);
            assertThatThrownBy(() -> concert.cancelSeat(id, CHULSOO, "A-1", 0))
                    .isInstanceOf(IllegalStateException.class);
            assertThat(concert.seats(id)).containsExactly("A-1");
        }

        @Test
        @DisplayName("4-5. 마지막 좌석까지 취소하면 예매가 사라진다")
        void lastSeat() {
            long id = concert.book(CHULSOO, List.of("A-1"));

            concert.cancelSeat(id, CHULSOO, "A-1", 5);

            assertThatThrownBy(() -> concert.total(id)).isInstanceOf(NoSuchElementException.class);
            assertThat(concert.isSold("A-1")).isFalse();
        }

        @Test
        @DisplayName("4-6. 부분 취소한 만큼 한도가 다시 생긴다")
        void cancelSeatFreesLimit() {
            long id = concert.book(CHULSOO, List.of("A-1", "A-2", "A-3", "A-4"));
            concert.cancelSeat(id, CHULSOO, "A-4", 5);

            assertThat(concert.book(CHULSOO, List.of("B-1"))).isEqualTo(2L);
        }

        @Test
        @DisplayName("4-7. 예매에 없는 좌석을 취소하면 IllegalArgumentException, 다른 사람이면 IllegalStateException")
        void cancelSeatErrors() {
            long id = concert.book(CHULSOO, List.of("A-1", "A-2"));
            concert.book(YOUNGHEE, List.of("B-1"));

            assertThatThrownBy(() -> concert.cancelSeat(id, CHULSOO, "B-1", 5))
                    .isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> concert.cancelSeat(id, CHULSOO, "Z-1", 5))
                    .isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> concert.cancelSeat(id, YOUNGHEE, "A-1", 5))
                    .isInstanceOf(IllegalStateException.class);
            assertThatThrownBy(() -> concert.cancelSeat(9, CHULSOO, "A-1", 5))
                    .isInstanceOf(NoSuchElementException.class);
            assertThat(concert.seats(id)).containsExactly("A-1", "A-2");
            assertThat(concert.isSold("B-1")).isTrue();
        }
    }
}
