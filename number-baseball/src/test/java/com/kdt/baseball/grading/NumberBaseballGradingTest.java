package com.kdt.baseball.grading;

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

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("숫자 야구 채점")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
class NumberBaseballGradingTest {
    private static final String PLAYING = "PLAYING";
    private static final String WIN = "WIN";
    private static final String LOSE = "LOSE";
    private static final int MAX_ATTEMPTS = 10;

    private NumberBaseballDriver game;

    @BeforeEach
    void setUp() {
        game = new MyNumberBaseballDriver();
    }

    private void guessWrong(int times) {
        for (int i = 0; i < times; i++) {
            game.guess("456");
        }
    }

    @Nested
    @Order(1)
    @DisplayName("1단계: 숫자 규칙")
    class Step1 {

        @ParameterizedTest(name = "1-1. 정답 {0}은 허용")
        @ValueSource(strings = {"123", "987", "159", "918"})
        void acceptsValidAnswer(String answer) {
            assertThatCode(() -> game.start(answer)).doesNotThrowAnyException();
        }

        @ParameterizedTest(name = "1-2. 정답 \"{0}\"은 IllegalArgumentException")
        @ValueSource(strings = {"", "1", "12", "1234", "102", "120", "012", "112", "121", "211", "999", "12a", "abc", "1 2", "-12"})
        void rejectsInvalidAnswer(String answer) {
            assertThatThrownBy(() -> game.start(answer))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @ParameterizedTest(name = "1-3. 추측 \"{0}\"은 IllegalArgumentException")
        @ValueSource(strings = {"", "12", "1234", "102", "112", "12a"})
        void rejectsInvalidGuess(String number) {
            game.start("123");

            assertThatThrownBy(() -> game.guess(number))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @Order(2)
    @DisplayName("2단계: 판정")
    class Step2 {

        @ParameterizedTest(name = "2-1. 정답 {0}, 추측 {1} → {2}")
        @CsvSource({
                "123, 123, 3스트라이크",
                "123, 124, 2스트라이크",
                "123, 183, 2스트라이크",
                "123, 189, 1스트라이크",
                "123, 132, 1스트라이크 2볼",
                "123, 321, 1스트라이크 2볼",
                "123, 913, 1스트라이크 1볼",
                "123, 312, 3볼",
                "123, 231, 3볼",
                "123, 234, 2볼",
                "123, 415, 1볼",
                "123, 456, 낫싱",
                "597, 975, 3볼",
                "597, 591, 2스트라이크",
                "597, 719, 2볼",
                "597, 123, 낫싱"
        })
        void judges(String answer, String guess, String expected) {
            game.start(answer);

            assertThat(game.guess(guess)).isEqualTo(expected);
        }

        @Test
        @DisplayName("2-2. 같은 추측을 반복해도 같은 결과")
        void sameGuessSameResult() {
            game.start("123");

            assertThat(game.guess("132")).isEqualTo("1스트라이크 2볼");
            assertThat(game.guess("132")).isEqualTo("1스트라이크 2볼");
        }

        @Test
        @DisplayName("2-3. 새 게임을 시작하면 새 정답으로 판정")
        void newGameNewAnswer() {
            game.start("123");
            game.start("456");

            assertThat(game.guess("456")).isEqualTo("3스트라이크");
        }
    }

    @Nested
    @Order(3)
    @DisplayName("3단계: 게임 진행")
    class Step3 {

        @BeforeEach
        void start() {
            game.start("123");
        }

        @Test
        @DisplayName("3-1. 시작하면 진행 중, 시도 0번")
        void startsPlaying() {
            assertThat(game.status()).isEqualTo(PLAYING);
            assertThat(game.attempts()).isZero();
        }

        @Test
        @DisplayName("3-2. 틀리면 시도 횟수가 늘고 계속 진행 중")
        void wrongGuessCounts() {
            guessWrong(3);

            assertThat(game.status()).isEqualTo(PLAYING);
            assertThat(game.attempts()).isEqualTo(3);
        }

        @Test
        @DisplayName("3-3. 첫 번째에 맞히면 승리, 시도 1번")
        void winAtFirst() {
            assertThat(game.guess("123")).isEqualTo("3스트라이크");
            assertThat(game.status()).isEqualTo(WIN);
            assertThat(game.attempts()).isEqualTo(1);
        }

        @Test
        @DisplayName("3-4. 규칙 위반 추측은 시도 횟수에 들어가지 않는다")
        void invalidGuessDoesNotCount() {
            guessWrong(2);

            assertThatThrownBy(() -> game.guess("112"))
                    .isInstanceOf(IllegalArgumentException.class);
            assertThat(game.attempts()).isEqualTo(2);
            assertThat(game.status()).isEqualTo(PLAYING);
        }

        @Test
        @DisplayName("3-5. 9번 틀린 뒤 10번째에 맞히면 승리")
        void winAtLastChance() {
            guessWrong(MAX_ATTEMPTS - 1);
            game.guess("123");

            assertThat(game.status()).isEqualTo(WIN);
            assertThat(game.attempts()).isEqualTo(MAX_ATTEMPTS);
        }

        @Test
        @DisplayName("3-6. 9번 틀리면 아직 진행 중")
        void stillPlayingAfterNine() {
            guessWrong(MAX_ATTEMPTS - 1);

            assertThat(game.status()).isEqualTo(PLAYING);
        }

        @Test
        @DisplayName("3-7. 10번 틀리면 패배")
        void loseAfterTen() {
            guessWrong(MAX_ATTEMPTS);

            assertThat(game.status()).isEqualTo(LOSE);
            assertThat(game.attempts()).isEqualTo(MAX_ATTEMPTS);
        }

        @Test
        @DisplayName("3-8. 승리한 게임에 추측하면 IllegalStateException, 횟수 그대로")
        void guessAfterWin() {
            game.guess("123");

            assertThatThrownBy(() -> game.guess("456"))
                    .isInstanceOf(IllegalStateException.class);
            assertThat(game.attempts()).isEqualTo(1);
            assertThat(game.status()).isEqualTo(WIN);
        }

        @Test
        @DisplayName("3-9. 패배한 게임에 정답을 추측해도 IllegalStateException")
        void guessAfterLose() {
            guessWrong(MAX_ATTEMPTS);

            assertThatThrownBy(() -> game.guess("123"))
                    .isInstanceOf(IllegalStateException.class);
            assertThat(game.status()).isEqualTo(LOSE);
        }

        @Test
        @DisplayName("3-10. 끝난 뒤 새 게임을 시작하면 다시 진행 중, 시도 0번")
        void restartAfterEnd() {
            game.guess("123");
            game.start("789");

            assertThat(game.status()).isEqualTo(PLAYING);
            assertThat(game.attempts()).isZero();
        }
    }

    @Nested
    @Order(4)
    @DisplayName("4단계: 정답 자동 생성")
    class Step4 {
        private static final int SAMPLES = 1000;

        @Test
        @DisplayName("4-1. 무작위 정답은 항상 1~9의 서로 다른 세 자리")
        void randomAnswerIsValid() {
            for (int i = 0; i < SAMPLES; i++) {
                String answer = game.randomAnswer();

                assertThat(answer).matches("[1-9]{3}");
                assertThat(answer.chars().distinct().count()).isEqualTo(3);
            }
        }

        @Test
        @DisplayName("4-2. 무작위 정답은 매번 같지 않다 (1000번 중 100가지 이상)")
        void randomAnswerVaries() {
            Set<String> answers = new HashSet<>();
            for (int i = 0; i < SAMPLES; i++) {
                answers.add(game.randomAnswer());
            }

            assertThat(answers).hasSizeGreaterThanOrEqualTo(100);
        }

        @Test
        @DisplayName("4-3. 무작위 정답으로 게임을 시작하고 맞힐 수 있다")
        void playWithRandomAnswer() {
            String answer = game.randomAnswer();
            game.start(answer);

            assertThat(game.guess(answer)).isEqualTo("3스트라이크");
            assertThat(game.status()).isEqualTo(WIN);
        }
    }
}
