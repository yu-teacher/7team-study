package com.kdt.library.grading;

import com.kdt.library.api.LibraryApi;
import com.kdt.library.api.LibraryController;
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

@DisplayName("도서 대출 채점")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
class LibraryGradingTest {
    private static final String CHULSOO = "철수";
    private static final String YOUNGHEE = "영희";
    private static final String MINSU = "민수";

    private LibraryApi library;

    @BeforeEach
    void setUp() {
        library = new LibraryController();
    }

    private void addBooks(int count) {
        for (int i = 1; i <= count; i++) {
            library.addBook("책" + i);
        }
    }

    private void lendAll(String member, int from, int to) {
        for (long id = from; id <= to; id++) {
            library.lend(id, member);
        }
    }

    @Nested
    @Order(1)
    @DisplayName("1단계: 책, 회원, 대출과 반납")
    class Step1 {

        @BeforeEach
        void members() {
            library.join(CHULSOO, "NORMAL");
            library.join(YOUNGHEE, "STUDENT");
        }

        @Test
        @DisplayName("1-1. 책 번호는 1부터 하나씩 늘어나고, 새 책은 빌릴 수 있다")
        void addBook() {
            assertThat(library.addBook("객체지향의 사실과 오해")).isEqualTo(1L);
            assertThat(library.addBook("오브젝트")).isEqualTo(2L);
            assertThat(library.available(1)).isTrue();
        }

        @ParameterizedTest(name = "1-2. 책 제목 \"{0}\"은 IllegalArgumentException, 번호를 쓰지 않는다")
        @ValueSource(strings = {"", "   "})
        void rejectsBlankTitle(String title) {
            assertThatThrownBy(() -> library.addBook(title))
                    .isInstanceOf(IllegalArgumentException.class);
            assertThat(library.addBook("오브젝트")).isEqualTo(1L);
        }

        @ParameterizedTest(name = "1-3. 이름 \"{0}\", 종류 \"{1}\"로 가입하면 IllegalArgumentException")
        @CsvSource({
                "'', NORMAL",
                "'  ', NORMAL",
                "민수, VIP",
                "민수, normal",
                "민수, ''"
        })
        void rejectsBadJoin(String name, String type) {
            assertThatThrownBy(() -> library.join(name, type))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("1-4. 같은 이름으로 또 가입하면 IllegalStateException")
        void rejectsDuplicate() {
            assertThatThrownBy(() -> library.join(CHULSOO, "STAFF"))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("1-5. 빌리면 빌릴 수 없는 책이 되고, 회원의 대출 목록에 책 번호 순서로 들어간다")
        void lend() {
            addBooks(3);

            library.lend(3, CHULSOO);
            library.lend(1, CHULSOO);

            assertThat(library.available(1)).isFalse();
            assertThat(library.available(2)).isTrue();
            assertThat(library.loans(CHULSOO)).containsExactly(1L, 3L);
            assertThat(library.loans(YOUNGHEE)).isEmpty();
        }

        @Test
        @DisplayName("1-6. 이미 빌려 간 책은 IllegalStateException")
        void rejectsLentBook() {
            addBooks(1);
            library.lend(1, CHULSOO);

            assertThatThrownBy(() -> library.lend(1, YOUNGHEE))
                    .isInstanceOf(IllegalStateException.class);
            assertThat(library.loans(YOUNGHEE)).isEmpty();
        }

        @Test
        @DisplayName("1-7. 반납하면 다시 빌릴 수 있다")
        void giveBack() {
            addBooks(1);
            library.lend(1, CHULSOO);

            library.giveBack(1);

            assertThat(library.available(1)).isTrue();
            assertThat(library.loans(CHULSOO)).isEmpty();
            library.lend(1, YOUNGHEE);
            assertThat(library.loans(YOUNGHEE)).containsExactly(1L);
        }

        @Test
        @DisplayName("1-8. 빌려 가지 않은 책을 반납하면 IllegalStateException")
        void giveBackNotLent() {
            addBooks(1);

            assertThatThrownBy(() -> library.giveBack(1))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("1-9. 없는 책이나 회원은 NoSuchElementException")
        void missing() {
            addBooks(1);

            assertThatThrownBy(() -> library.lend(9, CHULSOO)).isInstanceOf(NoSuchElementException.class);
            assertThatThrownBy(() -> library.lend(1, MINSU)).isInstanceOf(NoSuchElementException.class);
            assertThatThrownBy(() -> library.giveBack(9)).isInstanceOf(NoSuchElementException.class);
            assertThatThrownBy(() -> library.available(9)).isInstanceOf(NoSuchElementException.class);
            assertThatThrownBy(() -> library.loans(MINSU)).isInstanceOf(NoSuchElementException.class);
            assertThat(library.available(1)).isTrue();
        }
    }

    @Nested
    @Order(2)
    @DisplayName("2단계: 회원 종류별 한도와 반납 기한")
    class Step2 {

        @BeforeEach
        void members() {
            library.join(CHULSOO, "NORMAL");
            library.join(YOUNGHEE, "STUDENT");
            library.join(MINSU, "STAFF");
            addBooks(20);
        }

        @ParameterizedTest(name = "2-1. {0} 회원은 {1}권까지 빌린다")
        @CsvSource({
                "철수, 3",
                "영희, 5",
                "민수, 10"
        })
        void limit(String member, int max) {
            lendAll(member, 1, max);

            assertThatThrownBy(() -> library.lend(max + 1, member))
                    .isInstanceOf(IllegalStateException.class);
            assertThat(library.loans(member)).hasSize(max);
            assertThat(library.available(max + 1)).isTrue();
        }

        @Test
        @DisplayName("2-2. 반납하면 한도가 다시 생긴다")
        void giveBackFreesLimit() {
            lendAll(CHULSOO, 1, 3);
            library.giveBack(2);

            library.lend(4, CHULSOO);

            assertThat(library.loans(CHULSOO)).containsExactly(1L, 3L, 4L);
        }

        @ParameterizedTest(name = "2-3. {0} 회원이 2026-03-01에 빌리면 반납 기한은 {1}")
        @CsvSource({
                "철수, 2026-03-15",
                "영희, 2026-03-15",
                "민수, 2026-03-31"
        })
        void dueDate(String member, String due) {
            library.lend(1, member);

            assertThat(library.dueDate(1)).isEqualTo(due);
        }

        @Test
        @DisplayName("2-4. 처음 오늘은 2026-03-01이고, 오늘을 바꾸면 그날 기준으로 기한이 정해진다")
        void today() {
            library.today("2026-04-25");
            library.lend(1, CHULSOO);
            library.lend(2, MINSU);

            assertThat(library.dueDate(1)).isEqualTo("2026-05-09");
            assertThat(library.dueDate(2)).isEqualTo("2026-05-25");
        }

        @Test
        @DisplayName("2-5. 빌려 가지 않은 책의 기한을 물으면 IllegalStateException")
        void dueDateNotLent() {
            assertThatThrownBy(() -> library.dueDate(1))
                    .isInstanceOf(IllegalStateException.class);
            assertThatThrownBy(() -> library.dueDate(99))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @ParameterizedTest(name = "2-6. 날짜 \"{0}\"은 IllegalArgumentException")
        @ValueSource(strings = {"", "2026-3-5", "2026/03/05", "2026-02-30", "2026-13-01", "abc"})
        void rejectsBadDate(String date) {
            assertThatThrownBy(() -> library.today(date))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("2-7. 오늘은 거꾸로 갈 수 없다 (IllegalArgumentException)")
        void dateDoesNotGoBack() {
            library.today("2026-03-10");

            assertThatThrownBy(() -> library.today("2026-03-09"))
                    .isInstanceOf(IllegalArgumentException.class);
            library.today("2026-03-10");
        }
    }

    @Nested
    @Order(3)
    @DisplayName("3단계: 연체")
    class Step3 {

        @BeforeEach
        void members() {
            library.join(CHULSOO, "NORMAL");
            library.join(MINSU, "STAFF");
            addBooks(5);
        }

        @ParameterizedTest(name = "3-1. 기한 2026-03-15인 책을 {0}에 반납하면 연체료 {1}원")
        @CsvSource({
                "2026-03-10, 0",
                "2026-03-15, 0",
                "2026-03-16, 100",
                "2026-03-18, 300",
                "2026-04-14, 3000"
        })
        void lateFee(String returnedOn, int fee) {
            library.lend(1, CHULSOO);
            library.today(returnedOn);

            library.giveBack(1);

            assertThat(library.unpaidFee(CHULSOO)).isEqualTo(fee);
        }

        @Test
        @DisplayName("3-2. 처음 연체료는 0원이고, 여러 권의 연체료는 쌓인다")
        void feesAccumulate() {
            assertThat(library.unpaidFee(CHULSOO)).isZero();
            library.lend(1, CHULSOO);
            library.lend(2, CHULSOO);
            library.today("2026-03-17");

            library.giveBack(1);
            library.giveBack(2);

            assertThat(library.unpaidFee(CHULSOO)).isEqualTo(400);
        }

        @Test
        @DisplayName("3-3. 연체료가 남아 있으면 빌릴 수 없다 (IllegalStateException)")
        void unpaidBlocksLending() {
            library.lend(1, CHULSOO);
            library.today("2026-03-16");
            library.giveBack(1);

            assertThatThrownBy(() -> library.lend(2, CHULSOO))
                    .isInstanceOf(IllegalStateException.class);
            assertThat(library.available(2)).isTrue();
        }

        @Test
        @DisplayName("3-4. 연체료를 모두 내면 다시 빌릴 수 있다")
        void payThenLend() {
            library.lend(1, CHULSOO);
            library.today("2026-03-18");
            library.giveBack(1);

            library.pay(CHULSOO, 100);
            assertThat(library.unpaidFee(CHULSOO)).isEqualTo(200);
            assertThatThrownBy(() -> library.lend(2, CHULSOO))
                    .isInstanceOf(IllegalStateException.class);

            library.pay(CHULSOO, 200);
            library.lend(2, CHULSOO);
            assertThat(library.loans(CHULSOO)).containsExactly(2L);
        }

        @ParameterizedTest(name = "3-5. 연체료 300원에 {0}원을 내면 IllegalArgumentException, 금액 그대로")
        @ValueSource(ints = {0, -100, 301})
        void rejectsBadPayment(int amount) {
            library.lend(1, CHULSOO);
            library.today("2026-03-18");
            library.giveBack(1);

            assertThatThrownBy(() -> library.pay(CHULSOO, amount))
                    .isInstanceOf(IllegalArgumentException.class);
            assertThat(library.unpaidFee(CHULSOO)).isEqualTo(300);
        }

        @Test
        @DisplayName("3-6. 기한이 지난 책을 들고 있으면 반납 전이라도 새로 빌릴 수 없다")
        void overdueBlocksLending() {
            library.lend(1, CHULSOO);
            library.today("2026-03-16");

            assertThatThrownBy(() -> library.lend(2, CHULSOO))
                    .isInstanceOf(IllegalStateException.class);
            assertThat(library.unpaidFee(CHULSOO)).isZero();
        }

        @Test
        @DisplayName("3-7. 기한 당일까지는 다른 책을 빌릴 수 있다")
        void dueDayIsFine() {
            library.lend(1, CHULSOO);
            library.today("2026-03-15");

            library.lend(2, CHULSOO);

            assertThat(library.loans(CHULSOO)).containsExactly(1L, 2L);
        }

        @Test
        @DisplayName("3-8. 교직원도 기한(30일)이 지나면 연체료를 낸다")
        void staffAlsoPays() {
            library.lend(1, MINSU);
            library.today("2026-04-02");

            library.giveBack(1);

            assertThat(library.unpaidFee(MINSU)).isEqualTo(200);
        }

        @Test
        @DisplayName("3-9. 없는 회원의 연체료를 묻거나 내면 NoSuchElementException")
        void missingMember() {
            assertThatThrownBy(() -> library.unpaidFee(YOUNGHEE)).isInstanceOf(NoSuchElementException.class);
            assertThatThrownBy(() -> library.pay(YOUNGHEE, 100)).isInstanceOf(NoSuchElementException.class);
        }
    }

    @Nested
    @Order(4)
    @DisplayName("4단계: 예약")
    class Step4 {

        @BeforeEach
        void members() {
            library.join(CHULSOO, "NORMAL");
            library.join(YOUNGHEE, "STUDENT");
            library.join(MINSU, "STAFF");
            addBooks(3);
        }

        @Test
        @DisplayName("4-1. 대출 중인 책은 예약할 수 있고, 예약한 순서대로 줄을 선다")
        void reserve() {
            library.lend(1, CHULSOO);

            library.reserve(1, YOUNGHEE);
            library.reserve(1, MINSU);

            assertThat(library.reservations(1)).containsExactly(YOUNGHEE, MINSU);
            assertThat(library.reservations(2)).isEmpty();
        }

        @Test
        @DisplayName("4-2. 바로 빌릴 수 있는 책을 예약하면 IllegalStateException")
        void rejectsAvailable() {
            assertThatThrownBy(() -> library.reserve(1, YOUNGHEE))
                    .isInstanceOf(IllegalStateException.class);
            assertThat(library.reservations(1)).isEmpty();
        }

        @Test
        @DisplayName("4-3. 자기가 빌린 책이나 이미 예약한 책을 또 예약하면 IllegalStateException")
        void rejectsDuplicate() {
            library.lend(1, CHULSOO);
            library.reserve(1, YOUNGHEE);

            assertThatThrownBy(() -> library.reserve(1, CHULSOO)).isInstanceOf(IllegalStateException.class);
            assertThatThrownBy(() -> library.reserve(1, YOUNGHEE)).isInstanceOf(IllegalStateException.class);
            assertThat(library.reservations(1)).containsExactly(YOUNGHEE);
        }

        @Test
        @DisplayName("4-4. 예약된 책이 반납되면 아무나 빌릴 수 없고, 첫 번째 예약자만 빌릴 수 있다")
        void heldForFirst() {
            library.lend(1, CHULSOO);
            library.reserve(1, YOUNGHEE);
            library.reserve(1, MINSU);

            library.giveBack(1);

            assertThat(library.available(1)).isFalse();
            assertThatThrownBy(() -> library.lend(1, CHULSOO)).isInstanceOf(IllegalStateException.class);
            assertThatThrownBy(() -> library.lend(1, MINSU)).isInstanceOf(IllegalStateException.class);

            library.lend(1, YOUNGHEE);
            assertThat(library.loans(YOUNGHEE)).containsExactly(1L);
            assertThat(library.reservations(1)).containsExactly(MINSU);
        }

        @Test
        @DisplayName("4-5. 다음 예약자에게도 같은 규칙이 이어진다")
        void nextInLine() {
            library.lend(1, CHULSOO);
            library.reserve(1, YOUNGHEE);
            library.reserve(1, MINSU);
            library.giveBack(1);
            library.lend(1, YOUNGHEE);

            library.giveBack(1);

            assertThatThrownBy(() -> library.lend(1, CHULSOO)).isInstanceOf(IllegalStateException.class);
            library.lend(1, MINSU);
            assertThat(library.reservations(1)).isEmpty();
            library.giveBack(1);
            assertThat(library.available(1)).isTrue();
        }

        @Test
        @DisplayName("4-6. 반납 후 첫 예약자를 기다리는 동안 다른 사람도 예약 줄에 설 수 있다")
        void reserveWhileHeld() {
            library.lend(1, CHULSOO);
            library.reserve(1, YOUNGHEE);
            library.giveBack(1);

            library.reserve(1, CHULSOO);

            assertThat(library.reservations(1)).containsExactly(YOUNGHEE, CHULSOO);
        }

        @Test
        @DisplayName("4-7. 첫 예약자도 연체료가 있으면 빌릴 수 없고, 책은 계속 그 사람을 기다린다")
        void reserverStillFollowsRules() {
            library.lend(2, YOUNGHEE);
            library.lend(1, CHULSOO);
            library.reserve(1, YOUNGHEE);
            library.today("2026-03-16");
            library.giveBack(2);
            library.giveBack(1);

            assertThatThrownBy(() -> library.lend(1, YOUNGHEE)).isInstanceOf(IllegalStateException.class);
            assertThat(library.reservations(1)).containsExactly(YOUNGHEE);
            assertThatThrownBy(() -> library.lend(1, MINSU)).isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("4-8. 없는 책이나 회원으로 예약하면 NoSuchElementException")
        void missing() {
            library.lend(1, CHULSOO);

            assertThatThrownBy(() -> library.reserve(9, YOUNGHEE)).isInstanceOf(NoSuchElementException.class);
            assertThatThrownBy(() -> library.reserve(1, "없는사람")).isInstanceOf(NoSuchElementException.class);
            assertThatThrownBy(() -> library.reservations(9)).isInstanceOf(NoSuchElementException.class);
        }
    }
}
