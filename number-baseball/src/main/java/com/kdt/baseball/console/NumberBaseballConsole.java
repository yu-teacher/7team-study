package com.kdt.baseball.console;

import com.kdt.baseball.api.NumberBaseballApi;
import com.kdt.baseball.api.NumberBaseballController;

import java.util.Scanner;

/**
 * 직접 플레이해 보는 콘솔입니다. 채점하지 않으며 수정할 필요도 없습니다.
 * 채점 테스트와 똑같이 NumberBaseballApi(컨트롤러)만 부릅니다.
 */
public class NumberBaseballConsole {
    private final NumberBaseballApi game = new NumberBaseballController();
    private final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        new NumberBaseballConsole().run();
    }

    private void run() {
        System.out.println("숫자 야구! 1~9의 서로 다른 세 숫자를 10번 안에 맞혀 보세요. (끝내려면 q)");
        try {
            do {
                if (!playGame()) {
                    return;
                }
            } while (ask("다시 하시겠습니까? (y/n)").equalsIgnoreCase("y"));
        } catch (UnsupportedOperationException e) {
            System.out.println("아직 구현하지 않은 기능입니다 (" + e.getMessage() + "). 모든 단계를 구현한 뒤 실행해 보세요.");
        }
    }

    private boolean playGame() {
        String answer = game.randomAnswer();
        game.start(answer);
        while (game.status().equals("PLAYING")) {
            String input = ask("\n세 자리 숫자를 입력하세요:");
            if (input.equals("q")) {
                return false;
            }
            try {
                System.out.printf("%s (%d번째)%n", game.guess(input), game.attempts());
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("다시 입력해 주세요: " + e.getMessage());
            }
        }
        if (game.status().equals("WIN")) {
            System.out.printf("%n정답! %d번 만에 맞혔습니다. 🎉%n", game.attempts());
        } else {
            System.out.printf("%n10번 안에 맞히지 못했습니다. 정답은 %s였습니다.%n", answer);
        }
        return true;
    }

    private String ask(String message) {
        System.out.println(message);
        return scanner.hasNextLine() ? scanner.nextLine().trim() : "q";
    }
}
