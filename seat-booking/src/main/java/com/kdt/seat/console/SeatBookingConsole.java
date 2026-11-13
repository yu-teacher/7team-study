package com.kdt.seat.console;

import com.kdt.seat.api.SeatBookingApi;
import com.kdt.seat.api.SeatBookingController;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * 직접 써 보는 콘솔입니다. 채점하지 않으며 수정할 필요도 없습니다.
 * 채점 테스트와 똑같이 SeatBookingApi(컨트롤러)만 부릅니다.
 */
public class SeatBookingConsole {
    private static final String ROWS = "ABCDEFGHIJ";
    private static final String MENU = """

            [%s님] 1.좌석표 2.예매 3.예매 확인 4.전체 취소 5.좌석 하나 취소 6.사용자 바꾸기 0.끝내기""";

    private final SeatBookingApi concert = new SeatBookingController();
    private final Scanner scanner = new Scanner(System.in);
    private String user;

    public static void main(String[] args) {
        new SeatBookingConsole().run();
    }

    private void run() {
        System.out.println("공연 좌석 예매 (VIP A~B열 150,000원 / R C~F열 100,000원 / S G~J열 70,000원)");
        try {
            user = ask("이름을 입력하세요:");
            while (true) {
                String choice = ask(MENU.formatted(user));
                if (choice.equals("0")) {
                    return;
                }
                execute(choice);
            }
        } catch (InputEndedException e) {
            System.out.println("입력이 끝났습니다.");
        }
    }

    private void execute(String choice) {
        try {
            switch (choice) {
                case "1" -> printSeatMap();
                case "2" -> book();
                case "3" -> printBooking(bookingId());
                case "4" -> {
                    long id = bookingId();
                    int refund = concert.cancel(id, user, daysLeft());
                    System.out.printf("취소했습니다. 환불: %,d원%n", refund);
                }
                case "5" -> {
                    long id = bookingId();
                    int refund = concert.cancelSeat(id, user, ask("취소할 좌석:"), daysLeft());
                    System.out.printf("취소했습니다. 환불: %,d원%n", refund);
                }
                case "6" -> user = ask("이름을 입력하세요:");
                default -> System.out.println("메뉴 번호를 입력해 주세요.");
            }
        } catch (UnsupportedOperationException e) {
            System.out.println("아직 구현하지 않은 기능입니다 (" + e.getMessage() + ").");
        } catch (NumberFormatException e) {
            System.out.println("숫자로 입력해 주세요.");
        } catch (IllegalArgumentException e) {
            System.out.println("입력이 잘못되었습니다: " + e.getMessage());
        } catch (IllegalStateException e) {
            System.out.println("할 수 없습니다: " + e.getMessage());
        } catch (NoSuchElementException e) {
            System.out.println("찾을 수 없습니다: " + e.getMessage());
        }
    }

    private void printSeatMap() {
        StringBuilder header = new StringBuilder("     ");
        for (int number = 1; number <= 10; number++) {
            header.append(String.format("%2d ", number));
        }
        System.out.println(header + "  (■ 팔림, □ 빈 자리)");
        for (char row : ROWS.toCharArray()) {
            StringBuilder line = new StringBuilder(String.format("%c %-3s", row, concert.grade(row + "-1")));
            for (int number = 1; number <= 10; number++) {
                line.append(concert.isSold(row + "-" + number) ? " ■ " : " □ ");
            }
            System.out.println(line);
        }
    }

    private void book() {
        List<String> seats = Arrays.stream(ask("좌석을 쉼표로 구분해 입력하세요 (예: A-1,A-2):").split(","))
                                   .map(String::trim)
                                   .toList();
        long id = concert.book(user, seats);
        System.out.printf("%d번 예매 완료%n", id);
        printBooking(id);
    }

    private void printBooking(long id) {
        System.out.printf("%d번 예매: %s / 합계 %,d원%n", id, String.join(", ", concert.seats(id)), concert.total(id));
    }

    private long bookingId() {
        return Long.parseLong(ask("예매 번호:"));
    }

    private int daysLeft() {
        return Integer.parseInt(ask("공연까지 남은 날 (당일은 0):"));
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
