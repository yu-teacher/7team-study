package com.kdt.trafficlight.console;

import com.kdt.trafficlight.api.TrafficLightApi;
import com.kdt.trafficlight.api.TrafficLightController;

import java.util.Map;
import java.util.Scanner;

/**
 * 직접 조작해 보는 콘솔입니다. 채점하지 않으며 수정할 필요도 없습니다.
 * 채점 테스트와 똑같이 TrafficLightApi(컨트롤러)만 부릅니다.
 */
public class TrafficLightConsole {
    private static final Map<String, String> SIGNAL_NAME = Map.of(
            "RED", "🔴 빨강", "GREEN", "🟢 초록", "YELLOW", "🟡 노랑", "FLASHING_YELLOW", "⚠️ 노랑 깜빡임");
    private static final Map<String, String> PEDESTRIAN_NAME = Map.of("WALK", "건너세요", "STOP", "멈추세요");
    private static final String HELP = """
            명령어:
              next          다음 신호로 바꾸기
              wait 초       시간 흐르게 하기 (예: wait 10)
              button        보행자 버튼 누르기
              break         고장 내기
              repair        고치기
              q             끝내기""";

    private final TrafficLightApi light = new TrafficLightController();
    private final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        new TrafficLightConsole().run();
    }

    private void run() {
        System.out.println("신호등을 직접 조작해 보세요.");
        System.out.println(HELP);
        try {
            printState();
            while (true) {
                System.out.println("\n명령을 입력하세요:");
                if (!scanner.hasNextLine()) {
                    return;
                }
                String[] command = scanner.nextLine().trim().split("\\s+");
                if (command[0].equals("q")) {
                    return;
                }
                execute(command);
            }
        } catch (UnsupportedOperationException e) {
            System.out.println("아직 구현하지 않은 기능입니다 (" + e.getMessage() + "). 모든 단계를 구현한 뒤 실행해 보세요.");
        }
    }

    private void execute(String[] command) {
        try {
            switch (command[0]) {
                case "next" -> light.next();
                case "wait" -> light.elapse(Integer.parseInt(command[1]));
                case "button" -> light.pressPedestrianButton();
                case "break" -> light.breakDown();
                case "repair" -> light.repair();
                default -> {
                    System.out.println(HELP);
                    return;
                }
            }
            printState();
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("몇 초인지 함께 입력해 주세요. 예: wait 10");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("할 수 없습니다: " + e.getMessage());
        }
    }

    private void printState() {
        System.out.printf("차량: %s (%s) | 보행자: %s%n",
                SIGNAL_NAME.get(light.signal()), remaining(), PEDESTRIAN_NAME.get(light.pedestrianSignal()));
    }

    private String remaining() {
        try {
            return "남은 시간 " + light.remainingSeconds() + "초";
        } catch (IllegalStateException e) {
            return "남은 시간 없음";
        }
    }
}
