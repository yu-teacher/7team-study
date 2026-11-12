package com.kdt.rps.console;

import com.kdt.rps.api.RockPaperScissorsApi;
import com.kdt.rps.api.RockPaperScissorsController;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

/**
 * 직접 플레이해 보는 콘솔입니다. 채점하지 않으며 수정할 필요도 없습니다.
 * 채점 테스트와 똑같이 RockPaperScissorsApi(컨트롤러)만 부릅니다.
 */
public class RockPaperScissorsConsole {
    private static final Map<String, String> INPUT = Map.of("가위", "SCISSORS", "바위", "ROCK", "보", "PAPER");
    private static final Map<String, String> HAND_NAME = Map.of("SCISSORS", "가위", "ROCK", "바위", "PAPER", "보");
    private static final Map<String, String> RESULT_NAME = Map.of("WIN", "이겼습니다!", "LOSE", "졌습니다.", "DRAW", "비겼습니다.");
    private static final List<String> HANDS = List.of("ROCK", "PAPER", "SCISSORS");

    private final RockPaperScissorsApi rps = new RockPaperScissorsController();
    private final Scanner scanner = new Scanner(System.in);
    private final Random random = new Random();

    public static void main(String[] args) {
        new RockPaperScissorsConsole().run();
    }

    private void run() {
        System.out.println("가위바위보 3선승 경기! 컴퓨터와 대결합니다. (끝내려면 q)");
        do {
            if (!playMatch()) {
                return;
            }
        } while (ask("다시 하시겠습니까? (y/n)").equalsIgnoreCase("y"));
    }

    private boolean playMatch() {
        try {
            rps.start();
            while (rps.status().equals("PLAYING")) {
                String input = ask("\n가위, 바위, 보 중 하나를 내세요:");
                if (input.equals("q")) {
                    return false;
                }
                playRound(input);
            }
            System.out.println(rps.status().equals("WIN") ? "\n경기 승리! 🎉" : "\n경기 패배...");
            return true;
        } catch (UnsupportedOperationException e) {
            System.out.println("아직 구현하지 않은 기능입니다 (" + e.getMessage() + "). 모든 단계를 구현한 뒤 실행해 보세요.");
            return false;
        }
    }

    private void playRound(String input) {
        String me = INPUT.getOrDefault(input, input);
        String computer = HANDS.get(random.nextInt(HANDS.size()));
        try {
            String result = rps.play(me, computer);
            System.out.printf("나: %s, 컴퓨터: %s → %s%n", HAND_NAME.get(me), HAND_NAME.get(computer), RESULT_NAME.get(result));
            System.out.printf("전적: %d승 %d패 %d무%n", rps.wins(), rps.losses(), rps.draws());
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("다시 입력해 주세요: " + e.getMessage());
        }
    }

    private String ask(String message) {
        System.out.println(message);
        return scanner.hasNextLine() ? scanner.nextLine().trim() : "q";
    }
}
