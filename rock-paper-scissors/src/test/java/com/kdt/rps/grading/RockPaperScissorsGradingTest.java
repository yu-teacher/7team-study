package com.kdt.rps.grading;

import com.kdt.rps.api.RockPaperScissorsApi;
import com.kdt.rps.api.RockPaperScissorsController;
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
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("가위바위보 채점")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
class RockPaperScissorsGradingTest {
    private static final String ROCK = "ROCK";
    private static final String PAPER = "PAPER";
    private static final String SCISSORS = "SCISSORS";
    private static final List<String> HANDS = List.of(ROCK, PAPER, SCISSORS);

    private static final String WIN = "WIN";
    private static final String LOSE = "LOSE";
    private static final String DRAW = "DRAW";
    private static final String PLAYING = "PLAYING";

    private RockPaperScissorsApi rps;

    @BeforeEach
    void setUp() {
        rps = new RockPaperScissorsController();
    }

    private void win(int times) {
        for (int i = 0; i < times; i++) {
            rps.play(ROCK, SCISSORS);
        }
    }

    private void lose(int times) {
        for (int i = 0; i < times; i++) {
            rps.play(ROCK, PAPER);
        }
    }

    private void draw(int times) {
        for (int i = 0; i < times; i++) {
            rps.play(ROCK, ROCK);
        }
    }

    private void assertRecord(int wins, int losses, int draws) {
        assertThat(rps.wins()).isEqualTo(wins);
        assertThat(rps.losses()).isEqualTo(losses);
        assertThat(rps.draws()).isEqualTo(draws);
    }

    @Nested
    @Order(1)
    @DisplayName("1단계: 손 모양")
    class Step1 {

        @ParameterizedTest(name = "1-1. {0}은 {1}을 이긴다")
        @CsvSource({
                "ROCK, SCISSORS",
                "SCISSORS, PAPER",
                "PAPER, ROCK"
        })
        void winsAgainst(String hand, String expected) {
            assertThat(rps.winsAgainst(hand)).isEqualTo(expected);
        }

        @Test
        @DisplayName("1-2. 세 손은 각각 다른 손을 이긴다")
        void eachHandBeatsDifferentHand() {
            Set<String> beaten = HANDS.stream()
                                      .map(rps::winsAgainst)
                                      .collect(Collectors.toSet());

            assertThat(beaten).containsExactlyInAnyOrderElementsOf(HANDS);
        }

        @ParameterizedTest(name = "1-3. 손 \"{0}\"은 IllegalArgumentException")
        @ValueSource(strings = {"", "rock", "Rock", "가위", "LIZARD", " ROCK", "ROCK "})
        void rejectsInvalidHand(String hand) {
            assertThatThrownBy(() -> rps.winsAgainst(hand))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @Order(2)
    @DisplayName("2단계: 판정")
    class Step2 {

        @ParameterizedTest(name = "2-1. 나 {0}, 상대 {1} → {2}")
        @CsvSource({
                "ROCK, SCISSORS, WIN",
                "SCISSORS, PAPER, WIN",
                "PAPER, ROCK, WIN",
                "ROCK, PAPER, LOSE",
                "SCISSORS, ROCK, LOSE",
                "PAPER, SCISSORS, LOSE",
                "ROCK, ROCK, DRAW",
                "SCISSORS, SCISSORS, DRAW",
                "PAPER, PAPER, DRAW"
        })
        void judges(String me, String other, String expected) {
            assertThat(rps.judge(me, other)).isEqualTo(expected);
        }

        @Test
        @DisplayName("2-2. 나와 상대를 바꾸면 승패가 반대가 된다")
        void swapReversesResult() {
            for (String a : HANDS) {
                for (String b : HANDS) {
                    String result = rps.judge(a, b);
                    String swapped = rps.judge(b, a);

                    if (result.equals(WIN)) {
                        assertThat(swapped).isEqualTo(LOSE);
                    } else if (result.equals(LOSE)) {
                        assertThat(swapped).isEqualTo(WIN);
                    } else {
                        assertThat(swapped).isEqualTo(DRAW);
                    }
                }
            }
        }

        @ParameterizedTest(name = "2-3. 나 \"{0}\", 상대 \"{1}\"은 IllegalArgumentException")
        @CsvSource({
                "rock, SCISSORS",
                "ROCK, scissors",
                "LIZARD, ROCK",
                "ROCK, 보"
        })
        void rejectsInvalidHand(String me, String other) {
            assertThatThrownBy(() -> rps.judge(me, other))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @Order(3)
    @DisplayName("3단계: 전적")
    class Step3 {

        @BeforeEach
        void start() {
            rps.start();
        }

        @Test
        @DisplayName("3-1. 시작하면 0승 0패 0무")
        void startsEmpty() {
            assertRecord(0, 0, 0);
        }

        @Test
        @DisplayName("3-2. 한 판을 하면 결과를 돌려주고 전적에 쌓인다")
        void playReturnsResult() {
            assertThat(rps.play(ROCK, SCISSORS)).isEqualTo(WIN);
            assertThat(rps.play(ROCK, PAPER)).isEqualTo(LOSE);
            assertThat(rps.play(PAPER, PAPER)).isEqualTo(DRAW);

            assertRecord(1, 1, 1);
        }

        @Test
        @DisplayName("3-3. 여러 판을 하면 승, 패, 무를 각각 센다")
        void countsEachResult() {
            win(1);
            lose(1);
            draw(2);
            win(1);

            assertRecord(2, 1, 2);
        }

        @Test
        @DisplayName("3-4. 잘못된 손으로 하면 IllegalArgumentException, 전적 그대로")
        void invalidHandDoesNotCount() {
            win(1);

            assertThatThrownBy(() -> rps.play("rock", SCISSORS))
                    .isInstanceOf(IllegalArgumentException.class);
            assertRecord(1, 0, 0);
        }

        @Test
        @DisplayName("3-5. 새로 시작하면 전적이 0으로 돌아간다")
        void restartResetsRecord() {
            win(2);
            lose(1);
            rps.start();

            assertRecord(0, 0, 0);
        }
    }

    @Nested
    @Order(4)
    @DisplayName("4단계: 3선승 경기")
    class Step4 {

        @BeforeEach
        void start() {
            rps.start();
        }

        @Test
        @DisplayName("4-1. 시작하면 진행 중")
        void startsPlaying() {
            assertThat(rps.status()).isEqualTo(PLAYING);
        }

        @Test
        @DisplayName("4-2. 2번 이기고 2번 지면 아직 진행 중")
        void stillPlayingAtTwoTwo() {
            win(2);
            lose(2);

            assertThat(rps.status()).isEqualTo(PLAYING);
        }

        @Test
        @DisplayName("4-3. 먼저 3번 이기면 승리")
        void winAtThreeWins() {
            win(3);

            assertThat(rps.status()).isEqualTo(WIN);
        }

        @Test
        @DisplayName("4-4. 먼저 3번 지면 패배")
        void loseAtThreeLosses() {
            lose(3);

            assertThat(rps.status()).isEqualTo(LOSE);
        }

        @Test
        @DisplayName("4-5. 2승 2패에서 이기면 승리, 3승 2패")
        void winAtThreeTwo() {
            win(2);
            lose(2);
            win(1);

            assertThat(rps.status()).isEqualTo(WIN);
            assertRecord(3, 2, 0);
        }

        @Test
        @DisplayName("4-6. 비긴 판은 승부에 들어가지 않는다")
        void drawsDoNotDecide() {
            draw(10);
            win(2);

            assertThat(rps.status()).isEqualTo(PLAYING);
            assertRecord(2, 0, 10);
        }

        @Test
        @DisplayName("4-7. 끝난 경기에 하면 IllegalStateException, 전적 그대로")
        void playAfterEnd() {
            win(3);

            assertThatThrownBy(() -> rps.play(ROCK, PAPER))
                    .isInstanceOf(IllegalStateException.class);
            assertThat(rps.status()).isEqualTo(WIN);
            assertRecord(3, 0, 0);
        }

        @Test
        @DisplayName("4-8. 끝난 뒤 새로 시작하면 진행 중, 0승 0패 0무")
        void restartAfterEnd() {
            lose(3);
            rps.start();

            assertThat(rps.status()).isEqualTo(PLAYING);
            assertRecord(0, 0, 0);
        }
    }
}
