package com.kdt.lotto.console;

import com.kdt.lotto.api.LottoApi;
import com.kdt.lotto.api.LottoController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Supplier;

/**
 * 직접 사 보는 콘솔입니다. 채점하지 않으며 수정할 필요도 없습니다.
 * 채점 테스트와 똑같이 LottoApi(컨트롤러)만 부릅니다.
 */
public class LottoConsole {
    private static final List<String> RANKS = List.of("FIRST", "SECOND", "THIRD", "FOURTH", "FIFTH", "NONE");
    private static final Map<String, String> RANK_NAME = Map.of(
            "FIRST", "1등", "SECOND", "2등", "THIRD", "3등", "FOURTH", "4등", "FIFTH", "5등", "NONE", "꽝");

    private final LottoApi lotto = new LottoController();
    private final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        new LottoConsole().run();
    }

    private void run() {
        try {
            List<List<Integer>> tickets = retry(() -> lotto.buyAuto(Integer.parseInt(ask("구입 금액을 입력하세요 (1,000원 단위):"))));
            System.out.printf("%n%d장을 샀습니다.%n", tickets.size());
            tickets.forEach(System.out::println);

            retry(() -> {
                List<Integer> numbers = numbers(ask("\n당첨 번호 6개를 쉼표로 구분해 입력하세요 (예: 1,2,3,4,5,6):"));
                int bonus = Integer.parseInt(ask("보너스 번호를 입력하세요:"));
                lotto.winning(numbers, bonus);
                return null;
            });

            printResult(tickets);
        } catch (UnsupportedOperationException e) {
            System.out.println("아직 구현하지 않은 기능입니다 (" + e.getMessage() + "). 모든 단계를 구현한 뒤 실행해 보세요.");
        } catch (InputEndedException e) {
            System.out.println("입력이 끝났습니다.");
        }
    }

    private void printResult(List<List<Integer>> tickets) {
        System.out.println("\n당첨 결과");
        System.out.println("---------");
        for (String rank : RANKS) {
            long count = tickets.stream().filter(ticket -> lotto.rank(ticket).equals(rank)).count();
            System.out.printf("%s: %d장%n", RANK_NAME.get(rank), count);
        }
        long total = tickets.stream().mapToLong(lotto::prize).sum();
        System.out.printf("총 상금: %,d원%n", total);
        System.out.printf("수익률: %s%%%n", lotto.profitRate(tickets));
    }

    private List<Integer> numbers(String input) {
        if (input.isBlank()) {
            return List.of();
        }
        return Arrays.stream(input.split(","))
                     .map(String::trim)
                     .map(Integer::parseInt)
                     .toList();
    }

    private <T> T retry(Supplier<T> action) {
        while (true) {
            try {
                return action.get();
            } catch (IllegalArgumentException e) {
                System.out.println("다시 입력해 주세요: " + e.getMessage());
            }
        }
    }

    private String ask(String message) {
        System.out.println(message);
        if (!scanner.hasNextLine()) {
            throw new InputEndedException();
        }
        return scanner.nextLine().trim();
    }

    private static class InputEndedException extends RuntimeException {
    }
}
