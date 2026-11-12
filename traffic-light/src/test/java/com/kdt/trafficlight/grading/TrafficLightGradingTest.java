package com.kdt.trafficlight.grading;

import com.kdt.trafficlight.api.TrafficLightApi;
import com.kdt.trafficlight.api.TrafficLightController;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("신호등 채점")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
class TrafficLightGradingTest {
    private static final String RED = "RED";
    private static final String GREEN = "GREEN";
    private static final String YELLOW = "YELLOW";
    private static final String FLASHING_YELLOW = "FLASHING_YELLOW";
    private static final String WALK = "WALK";
    private static final String STOP = "STOP";

    private TrafficLightApi light;

    @BeforeEach
    void setUp() {
        light = new TrafficLightController();
    }

    private void nextTimes(int times) {
        for (int i = 0; i < times; i++) {
            light.next();
        }
    }

    @Nested
    @Order(1)
    @DisplayName("1단계: 신호 바꾸기")
    class Step1 {

        @Test
        @DisplayName("1-1. 처음 신호는 빨강")
        void startsWithRed() {
            assertThat(light.signal()).isEqualTo(RED);
        }

        @Test
        @DisplayName("1-2. 빨강 다음은 초록")
        void redToGreen() {
            nextTimes(1);
            assertThat(light.signal()).isEqualTo(GREEN);
        }

        @Test
        @DisplayName("1-3. 초록 다음은 노랑")
        void greenToYellow() {
            nextTimes(2);
            assertThat(light.signal()).isEqualTo(YELLOW);
        }

        @Test
        @DisplayName("1-4. 노랑 다음은 다시 빨강")
        void yellowToRed() {
            nextTimes(3);
            assertThat(light.signal()).isEqualTo(RED);
        }

        @Test
        @DisplayName("1-5. 여러 바퀴를 돌아도 순서가 유지된다 (10번 → 초록)")
        void keepsOrderOverManyCycles() {
            nextTimes(10);
            assertThat(light.signal()).isEqualTo(GREEN);
        }

        @Test
        @DisplayName("1-6. 신호를 확인하기만 해서는 바뀌지 않는다")
        void readingDoesNotChange() {
            light.signal();
            light.signal();
            assertThat(light.signal()).isEqualTo(RED);
        }
    }

    @Nested
    @Order(2)
    @DisplayName("2단계: 시간이 흐르면 바뀌기")
    class Step2 {

        @Test
        @DisplayName("2-1. 처음 남은 시간은 30초")
        void startsWith30Seconds() {
            assertThat(light.remainingSeconds()).isEqualTo(30);
        }

        @ParameterizedTest(name = "2-2. {0}초 흐르면 → {1} {2}초 남음")
        @CsvSource({
                "1,  RED,    29",
                "29, RED,    1",
                "30, GREEN,  25",
                "31, GREEN,  24",
                "54, GREEN,  1",
                "55, YELLOW, 5",
                "59, YELLOW, 1",
                "60, RED,    30",
                "61, RED,    29",
                "90, GREEN,  25",
                "600, RED,   30",
                "607, RED,   23"
        })
        void elapseOnce(int seconds, String signal, int remaining) {
            light.elapse(seconds);

            assertThat(light.signal()).isEqualTo(signal);
            assertThat(light.remainingSeconds()).isEqualTo(remaining);
        }

        @Test
        @DisplayName("2-3. 시간을 나눠서 흘려도 한 번에 흘린 것과 같다 (10+25+10 = 45초)")
        void splitElapseEqualsSingle() {
            light.elapse(10);
            light.elapse(25);
            light.elapse(10);

            assertThat(light.signal()).isEqualTo(GREEN);
            assertThat(light.remainingSeconds()).isEqualTo(10);
        }

        @Test
        @DisplayName("2-4. 1초씩 60번 흘리면 빨강 30초로 돌아온다")
        void oneSecondAtATime() {
            for (int i = 0; i < 60; i++) {
                light.elapse(1);
            }

            assertThat(light.signal()).isEqualTo(RED);
            assertThat(light.remainingSeconds()).isEqualTo(30);
        }

        @Test
        @DisplayName("2-5. 수동으로 바꾸면 그 신호의 시간으로 다시 시작한다")
        void manualChangeResetsTime() {
            light.elapse(10);
            light.next();

            assertThat(light.signal()).isEqualTo(GREEN);
            assertThat(light.remainingSeconds()).isEqualTo(25);
        }

        @Test
        @DisplayName("2-6. 수동 변경 후에도 시간 흐름이 이어진다 (초록에서 27초 → 노랑 3초)")
        void elapseAfterManualChange() {
            light.next();
            light.elapse(27);

            assertThat(light.signal()).isEqualTo(YELLOW);
            assertThat(light.remainingSeconds()).isEqualTo(3);
        }

        @ParameterizedTest(name = "2-7. {0}초는 IllegalArgumentException")
        @ValueSource(ints = {0, -1, -30})
        void rejectsNonPositiveSeconds(int seconds) {
            assertThatThrownBy(() -> light.elapse(seconds))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("2-8. 예외가 난 뒤에도 상태는 그대로다")
        void stateUnchangedAfterException() {
            light.elapse(10);

            assertThatThrownBy(() -> light.elapse(-5))
                    .isInstanceOf(IllegalArgumentException.class);
            assertThat(light.signal()).isEqualTo(RED);
            assertThat(light.remainingSeconds()).isEqualTo(20);
        }
    }

    @Nested
    @Order(3)
    @DisplayName("3단계: 보행자 신호")
    class Step3 {

        @Test
        @DisplayName("3-1. 차량 빨강이면 보행자는 건너세요")
        void walkOnRed() {
            assertThat(light.pedestrianSignal()).isEqualTo(WALK);
        }

        @Test
        @DisplayName("3-2. 차량 초록이면 보행자는 멈추세요")
        void stopOnGreen() {
            nextTimes(1);
            assertThat(light.pedestrianSignal()).isEqualTo(STOP);
        }

        @Test
        @DisplayName("3-3. 차량 노랑이면 보행자는 멈추세요")
        void stopOnYellow() {
            nextTimes(2);
            assertThat(light.pedestrianSignal()).isEqualTo(STOP);
        }

        @Test
        @DisplayName("3-4. 시간이 흘러 빨강이 되면 보행자는 다시 건너세요")
        void walkAgainAfterCycle() {
            light.elapse(60);
            assertThat(light.pedestrianSignal()).isEqualTo(WALK);
        }

        @Test
        @DisplayName("3-5. 초록 25초에서 버튼 → 5초로 준다")
        void buttonShortensGreen() {
            nextTimes(1);
            light.pressPedestrianButton();

            assertThat(light.signal()).isEqualTo(GREEN);
            assertThat(light.remainingSeconds()).isEqualTo(5);
        }

        @Test
        @DisplayName("3-6. 초록 6초 남았을 때 버튼 → 5초")
        void buttonAtSixSeconds() {
            nextTimes(1);
            light.elapse(19);
            light.pressPedestrianButton();

            assertThat(light.remainingSeconds()).isEqualTo(5);
        }

        @Test
        @DisplayName("3-7. 초록 5초 남았을 때 버튼 → 그대로 5초")
        void buttonAtFiveSeconds() {
            nextTimes(1);
            light.elapse(20);
            light.pressPedestrianButton();

            assertThat(light.remainingSeconds()).isEqualTo(5);
        }

        @Test
        @DisplayName("3-8. 초록 3초 남았을 때 버튼 → 그대로 3초 (늘어나지 않음)")
        void buttonDoesNotExtend() {
            nextTimes(1);
            light.elapse(22);
            light.pressPedestrianButton();

            assertThat(light.remainingSeconds()).isEqualTo(3);
        }

        @Test
        @DisplayName("3-9. 버튼을 여러 번 눌러도 5초")
        void buttonPressedManyTimes() {
            nextTimes(1);
            light.pressPedestrianButton();
            light.pressPedestrianButton();
            light.pressPedestrianButton();

            assertThat(light.remainingSeconds()).isEqualTo(5);
        }

        @Test
        @DisplayName("3-10. 버튼 후 5초가 지나면 노랑 5초")
        void yellowAfterButton() {
            nextTimes(1);
            light.pressPedestrianButton();
            light.elapse(5);

            assertThat(light.signal()).isEqualTo(YELLOW);
            assertThat(light.remainingSeconds()).isEqualTo(5);
        }

        @Test
        @DisplayName("3-11. 빨강에서 버튼은 아무 일도 하지 않는다")
        void buttonIgnoredOnRed() {
            light.elapse(10);
            light.pressPedestrianButton();

            assertThat(light.signal()).isEqualTo(RED);
            assertThat(light.remainingSeconds()).isEqualTo(20);
        }

        @Test
        @DisplayName("3-12. 노랑에서 버튼은 아무 일도 하지 않는다")
        void buttonIgnoredOnYellow() {
            nextTimes(2);
            light.pressPedestrianButton();

            assertThat(light.signal()).isEqualTo(YELLOW);
            assertThat(light.remainingSeconds()).isEqualTo(5);
        }
    }

    @Nested
    @Order(4)
    @DisplayName("4단계 (선택): 고장 모드")
    class Step4 {

        @Test
        @DisplayName("4-1. 고장 나면 노랑 점멸")
        void flashesYellow() {
            light.breakDown();
            assertThat(light.signal()).isEqualTo(FLASHING_YELLOW);
        }

        @Test
        @DisplayName("4-2. 어떤 신호에서 고장 나도 노랑 점멸 (초록에서 고장)")
        void flashesYellowFromGreen() {
            nextTimes(1);
            light.breakDown();
            assertThat(light.signal()).isEqualTo(FLASHING_YELLOW);
        }

        @Test
        @DisplayName("4-3. 고장 중에는 시간이 흘러도 그대로")
        void ignoresElapse() {
            light.breakDown();
            light.elapse(100);
            assertThat(light.signal()).isEqualTo(FLASHING_YELLOW);
        }

        @Test
        @DisplayName("4-4. 고장 중에는 수동으로 바꿔도 그대로")
        void ignoresNext() {
            light.breakDown();
            light.next();
            assertThat(light.signal()).isEqualTo(FLASHING_YELLOW);
        }

        @Test
        @DisplayName("4-5. 고장 중 보행자는 멈추세요 (빨강에서 고장 나도)")
        void pedestrianStops() {
            light.breakDown();
            assertThat(light.pedestrianSignal()).isEqualTo(STOP);
        }

        @Test
        @DisplayName("4-6. 고장 중 버튼을 눌러도 복구 후 영향 없음")
        void buttonIgnoredWhileBroken() {
            nextTimes(1);
            light.breakDown();
            light.pressPedestrianButton();
            light.repair();

            assertThat(light.signal()).isEqualTo(RED);
            assertThat(light.remainingSeconds()).isEqualTo(30);
        }

        @Test
        @DisplayName("4-7. 복구하면 빨강 30초부터 다시 시작")
        void repairRestartsFromRed() {
            nextTimes(2);
            light.elapse(2);
            light.breakDown();
            light.repair();

            assertThat(light.signal()).isEqualTo(RED);
            assertThat(light.remainingSeconds()).isEqualTo(30);
            assertThat(light.pedestrianSignal()).isEqualTo(WALK);
        }

        @Test
        @DisplayName("4-8. 복구 후에는 다시 정상 동작한다 (30초 → 초록)")
        void worksAfterRepair() {
            light.breakDown();
            light.repair();
            light.elapse(30);

            assertThat(light.signal()).isEqualTo(GREEN);
            assertThat(light.remainingSeconds()).isEqualTo(25);
        }
    }
}
