package com.kdt.lotto.grading;

import com.kdt.lotto.api.LottoApi;
import com.kdt.lotto.api.LottoController;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("로또 채점")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
class LottoGradingTest {
    private static final List<Integer> WINNING = List.of(1, 2, 3, 4, 5, 6);
    private static final int BONUS = 7;

    private LottoApi lotto;

    @BeforeEach
    void setUp() {
        lotto = new LottoController();
    }

    private static List<Integer> nums(String csv) {
        if (csv == null || csv.isBlank()) {
            return List.of();
        }
        return Arrays.stream(csv.split("[ ,]+"))
                     .map(String::trim)
                     .map(Integer::parseInt)
                     .toList();
    }

    private static List<List<Integer>> tickets(String... tickets) {
        List<List<Integer>> result = new ArrayList<>();
        for (String ticket : tickets) {
            result.add(nums(ticket));
        }
        return result;
    }

    @Nested
    @Order(1)
    @DisplayName("1단계: 로또 한 장")
    class Step1 {

        @ParameterizedTest(name = "1-1. [{0}] → [{1}]")
        @CsvSource({
                "'1 2 3 4 5 6',        '1 2 3 4 5 6'",
                "'43 8 42 21 41 23',   '8 21 23 41 42 43'",
                "'45 44 43 42 41 40',  '40 41 42 43 44 45'",
                "'1 45 2 44 3 43',     '1 2 3 43 44 45'"
        })
        void sortsNumbers(String input, String expected) {
            assertThat(lotto.ticket(nums(input))).containsExactlyElementsOf(nums(expected));
        }

        @ParameterizedTest(name = "1-2. [{0}]은 IllegalArgumentException")
        @ValueSource(strings = {
                "",
                "1 2 3 4 5",
                "1 2 3 4 5 6 7",
                "0 1 2 3 4 5",
                "1 2 3 4 5 46",
                "-1 2 3 4 5 6",
                "1 1 2 3 4 5",
                "1 2 3 4 5 5",
                "7 7 7 7 7 7"
        })
        void rejectsInvalidTicket(String input) {
            assertThatThrownBy(() -> lotto.ticket(nums(input)))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @Order(2)
    @DisplayName("2단계: 당첨 번호")
    class Step2 {

        @Test
        @DisplayName("2-1. 번호 6개 + 보너스 1개는 허용")
        void acceptsValidWinning() {
            assertThatCode(() -> lotto.winning(WINNING, BONUS)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("2-2. 경계값 1과 45 보너스도 허용")
        void acceptsBoundaryBonus() {
            assertThatCode(() -> lotto.winning(nums("2 3 4 5 6 7"), 1)).doesNotThrowAnyException();
            assertThatCode(() -> lotto.winning(nums("2 3 4 5 6 7"), 45)).doesNotThrowAnyException();
        }

        @ParameterizedTest(name = "2-3. 번호 [{0}]은 IllegalArgumentException")
        @ValueSource(strings = {"1 2 3 4 5", "0 1 2 3 4 5", "1 2 3 4 5 46", "1 1 2 3 4 5"})
        void rejectsInvalidNumbers(String input) {
            assertThatThrownBy(() -> lotto.winning(nums(input), 45))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @ParameterizedTest(name = "2-4. 보너스 {0}은 IllegalArgumentException")
        @ValueSource(ints = {0, 46, -1, 1, 6})
        void rejectsInvalidBonus(int bonus) {
            assertThatThrownBy(() -> lotto.winning(WINNING, bonus))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @Order(3)
    @DisplayName("3단계: 등수와 상금 (당첨 1 2 3 4 5 6 + 보너스 7)")
    class Step3 {

        @BeforeEach
        void setWinning() {
            lotto.winning(WINNING, BONUS);
        }

        @ParameterizedTest(name = "3-1. [{0}] → {1}, {2}원")
        @CsvSource({
                "'1 2 3 4 5 6',        FIRST,  2000000000",
                "'6 5 4 3 2 1',        FIRST,  2000000000",
                "'1 2 3 4 5 7',        SECOND, 30000000",
                "'7 5 4 3 2 1',        SECOND, 30000000",
                "'1 2 3 4 5 8',        THIRD,  1500000",
                "'1 2 3 4 5 45',       THIRD,  1500000",
                "'1 2 3 4 8 9',        FOURTH, 50000",
                "'1 2 3 4 7 8',        FOURTH, 50000",
                "'1 2 3 8 9 10',       FIFTH,  5000",
                "'1 2 3 7 8 9',        FIFTH,  5000",
                "'1 2 8 9 10 11',      NONE,   0",
                "'1 2 7 8 9 10',       NONE,   0",
                "'7 8 9 10 11 12',     NONE,   0",
                "'40 41 42 43 44 45',  NONE,   0"
        })
        void ranks(String ticket, String rank, long prize) {
            assertThat(lotto.rank(nums(ticket))).isEqualTo(rank);
            assertThat(lotto.prize(nums(ticket))).isEqualTo(prize);
        }

        @Test
        @DisplayName("3-2. 당첨 번호를 바꾸면 바뀐 번호로 판정")
        void changesWinning() {
            lotto.winning(nums("40 41 42 43 44 45"), 1);

            assertThat(lotto.rank(nums("40 41 42 43 44 45"))).isEqualTo("FIRST");
            assertThat(lotto.rank(nums("1 2 3 4 5 6"))).isEqualTo("NONE");
        }

        @Test
        @DisplayName("3-3. 잘못된 번호로는 등수를 확인할 수 없다")
        void rejectsInvalidTicket() {
            assertThatThrownBy(() -> lotto.rank(nums("1 1 2 3 4 5")))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @Order(4)
    @DisplayName("4단계: 구매와 수익률")
    class Step4 {

        @ParameterizedTest(name = "4-1. {0}원 → {1}장")
        @CsvSource({"1000, 1", "14000, 14", "100000, 100"})
        void buysByMoney(int money, int count) {
            assertThat(lotto.buyAuto(money)).hasSize(count);
        }

        @ParameterizedTest(name = "4-2. {0}원은 IllegalArgumentException")
        @ValueSource(ints = {0, 999, 1500, 14001, -1000, 101000})
        void rejectsInvalidMoney(int money) {
            assertThatThrownBy(() -> lotto.buyAuto(money))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("4-3. 자동 발급 번호는 모두 규칙에 맞고 오름차순")
        void autoTicketsAreValid() {
            for (List<Integer> ticket : lotto.buyAuto(100000)) {
                assertThat(ticket).hasSize(6)
                                  .doesNotHaveDuplicates()
                                  .allMatch(n -> n >= 1 && n <= 45)
                                  .isSorted();
            }
        }

        @Test
        @DisplayName("4-4. 자동 발급 번호는 매번 같지 않다 (100장 중 95가지 이상)")
        void autoTicketsVary() {
            assertThat(new HashSet<>(lotto.buyAuto(100000))).hasSizeGreaterThanOrEqualTo(95);
        }

        @Test
        @DisplayName("4-5. 5등 1장 → 500.0")
        void profitOneFifth() {
            lotto.winning(WINNING, BONUS);

            assertThat(lotto.profitRate(tickets("1 2 3 10 11 12"))).isEqualTo(500.0);
        }

        @Test
        @DisplayName("4-6. 8장 중 5등 1장 → 62.5")
        void profitEightTickets() {
            lotto.winning(WINNING, BONUS);
            List<List<Integer>> bought = tickets(
                    "1 2 3 10 11 12",
                    "10 11 12 13 14 15", "16 17 18 19 20 21", "22 23 24 25 26 27",
                    "28 29 30 31 32 33", "34 35 36 37 38 39", "40 41 42 43 44 45",
                    "8 9 10 11 12 13");

            assertThat(lotto.profitRate(bought)).isEqualTo(62.5);
        }

        @Test
        @DisplayName("4-7. 3장 중 5등 1장 → 166.7 (반올림 올림)")
        void profitRoundsUp() {
            lotto.winning(WINNING, BONUS);
            List<List<Integer>> bought = tickets("1 2 3 10 11 12", "10 11 12 13 14 15", "16 17 18 19 20 21");

            assertThat(lotto.profitRate(bought)).isEqualTo(166.7);
        }

        @Test
        @DisplayName("4-8. 7장 중 5등 1장 → 71.4 (반올림 내림)")
        void profitRoundsDown() {
            lotto.winning(WINNING, BONUS);
            List<List<Integer>> bought = tickets(
                    "1 2 3 10 11 12",
                    "10 11 12 13 14 15", "16 17 18 19 20 21", "22 23 24 25 26 27",
                    "28 29 30 31 32 33", "34 35 36 37 38 39", "40 41 42 43 44 45");

            assertThat(lotto.profitRate(bought)).isEqualTo(71.4);
        }

        @Test
        @DisplayName("4-9. 모두 꽝 → 0.0")
        void profitZero() {
            lotto.winning(WINNING, BONUS);
            List<List<Integer>> bought = tickets("10 11 12 13 14 15", "16 17 18 19 20 21", "1 2 30 31 32 33");

            assertThat(lotto.profitRate(bought)).isEqualTo(0.0);
        }

        @Test
        @DisplayName("4-10. 2등 + 3등 → 1575000.0 (여러 등수 합산)")
        void profitSumsPrizes() {
            lotto.winning(WINNING, BONUS);
            List<List<Integer>> bought = tickets("1 2 3 4 5 7", "1 2 3 4 5 8");

            assertThat(lotto.profitRate(bought)).isEqualTo(1575000.0);
        }

        @Test
        @DisplayName("4-11. 1등 1장 → 200000000.0 (큰 금액도 넘치지 않음)")
        void profitFirst() {
            lotto.winning(WINNING, BONUS);

            assertThat(lotto.profitRate(tickets("1 2 3 4 5 6"))).isEqualTo(200000000.0);
        }

        @Test
        @DisplayName("4-12. 1등 2장 → 200000000.0 (상금 합계가 int 범위를 넘어도 정확)")
        void profitTwoFirsts() {
            lotto.winning(WINNING, BONUS);

            assertThat(lotto.profitRate(tickets("1 2 3 4 5 6", "6 5 4 3 2 1"))).isEqualTo(200000000.0);
        }
    }
}
