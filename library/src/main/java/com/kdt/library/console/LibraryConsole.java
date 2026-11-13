package com.kdt.library.console;

import com.kdt.library.api.LibraryApi;
import com.kdt.library.api.LibraryController;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * 직접 써 보는 콘솔입니다. 채점하지 않으며 수정할 필요도 없습니다.
 * 채점 테스트와 똑같이 LibraryApi(컨트롤러)만 부릅니다.
 */
public class LibraryConsole {
    private static final String MENU = """

            [오늘 %s] 1.책 등록 2.회원 가입 3.책 목록 4.대출 5.반납 6.예약 7.내 정보 8.연체료 내기 9.날짜 바꾸기 0.끝내기""";

    private final LibraryApi library = new LibraryController();
    private final Scanner scanner = new Scanner(System.in);
    // 목록을 보여 주기 위해 콘솔이 책 제목을 기억해 둡니다.
    private final List<String> titles = new ArrayList<>();
    private String today = "2026-03-01";

    public static void main(String[] args) {
        new LibraryConsole().run();
    }

    private void run() {
        System.out.println("도서관 (회원 종류: NORMAL 3권/14일, STUDENT 5권/14일, STAFF 10권/30일)");
        try {
            while (true) {
                String choice = ask(MENU.formatted(today));
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
                case "1" -> addBook();
                case "2" -> {
                    library.join(ask("이름:"), ask("종류 (NORMAL, STUDENT, STAFF):"));
                    System.out.println("가입했습니다.");
                }
                case "3" -> printBooks();
                case "4" -> {
                    long id = bookId();
                    library.lend(id, ask("빌리는 회원:"));
                    System.out.printf("빌려 갔습니다. 반납 기한: %s%n", library.dueDate(id));
                }
                case "5" -> {
                    library.giveBack(bookId());
                    System.out.println("반납했습니다.");
                }
                case "6" -> {
                    long id = bookId();
                    library.reserve(id, ask("예약하는 회원:"));
                    System.out.println("예약 순서: " + library.reservations(id));
                }
                case "7" -> printMember(ask("회원 이름:"));
                case "8" -> {
                    String member = ask("회원 이름:");
                    library.pay(member, Integer.parseInt(ask("낼 금액:")));
                    System.out.printf("남은 연체료: %d원%n", library.unpaidFee(member));
                }
                case "9" -> {
                    String date = ask("날짜 (예: 2026-03-20):");
                    library.today(date);
                    today = date;
                }
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

    private void addBook() {
        String title = ask("책 제목:");
        long id = library.addBook(title);
        titles.add(title);
        System.out.println(id + "번 책을 등록했습니다.");
    }

    private void printBooks() {
        for (int i = 0; i < titles.size(); i++) {
            long id = i + 1;
            String state;
            if (library.available(id)) {
                state = "대출 가능";
            } else if (!library.reservations(id).isEmpty() && isWaiting(id)) {
                state = "예약자 대기 " + library.reservations(id);
            } else {
                state = "대출 중 (기한 " + library.dueDate(id) + ")" + reservationText(id);
            }
            System.out.printf("%d. %s - %s%n", id, titles.get(i), state);
        }
    }

    private boolean isWaiting(long id) {
        try {
            library.dueDate(id);
            return false;
        } catch (IllegalStateException e) {
            return true;
        }
    }

    private String reservationText(long id) {
        List<String> waiting = library.reservations(id);
        return waiting.isEmpty() ? "" : " 예약 " + waiting;
    }

    private void printMember(String member) {
        List<Long> loans = library.loans(member);
        System.out.printf("빌린 책: %s%n", loans.isEmpty() ? "없음" : loans);
        for (long id : loans) {
            System.out.printf("  %d. %s (기한 %s)%n", id, titles.get((int) id - 1), library.dueDate(id));
        }
        System.out.printf("연체료: %d원%n", library.unpaidFee(member));
    }

    private long bookId() {
        return Long.parseLong(ask("책 번호:"));
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
