package com.kdt.meetingroom.console;

import com.kdt.meetingroom.api.MeetingRoomApi;
import com.kdt.meetingroom.api.MeetingRoomController;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * 직접 써 보는 콘솔입니다. 채점하지 않으며 수정할 필요도 없습니다.
 * 채점 테스트와 똑같이 MeetingRoomApi(컨트롤러)만 부릅니다.
 */
public class MeetingRoomConsole {
    private static final List<String> ROOMS = List.of("A", "B", "C");
    private static final String MENU = """

            [%s님 | 지금 %s] 1.예약 2.일정 3.취소 4.내 사용 시간 5.시간 흐르기 6.사용자 바꾸기 0.끝내기""";

    private final MeetingRoomApi rooms = new MeetingRoomController();
    private final Scanner scanner = new Scanner(System.in);
    private String user;
    private String now = "09:00";

    public static void main(String[] args) {
        new MeetingRoomConsole().run();
    }

    private void run() {
        try {
            System.out.println("회의실 예약 (회의실 A, B, C / 09:00~18:00 / 30분 단위)");
            user = ask("이름을 입력하세요:");
            while (true) {
                String choice = ask(MENU.formatted(user, now));
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
                case "1" -> reserve();
                case "2" -> printSchedule();
                case "3" -> {
                    rooms.cancel(Long.parseLong(ask("취소할 예약 번호:")), user);
                    System.out.println("취소했습니다.");
                }
                case "4" -> System.out.printf("오늘 예약한 시간: %d분 / 120분%n", rooms.usedMinutes(user));
                case "5" -> {
                    String time = ask("몇 시로 바꿀까요? (예: 13:30)");
                    rooms.now(time);
                    now = time;
                }
                case "6" -> user = ask("이름을 입력하세요:");
                default -> System.out.println("메뉴 번호를 입력해 주세요.");
            }
        } catch (UnsupportedOperationException e) {
            System.out.println("아직 구현하지 않은 기능입니다 (" + e.getMessage() + ").");
        } catch (NumberFormatException e) {
            System.out.println("번호를 숫자로 입력해 주세요.");
        } catch (IllegalArgumentException e) {
            System.out.println("입력이 잘못되었습니다: " + e.getMessage());
        } catch (IllegalStateException e) {
            System.out.println("할 수 없습니다: " + e.getMessage());
        } catch (NoSuchElementException e) {
            System.out.println("찾을 수 없습니다: " + e.getMessage());
        }
    }

    private void reserve() {
        String room = ask("회의실 (A, B, C):");
        String start = ask("시작 시각 (예: 10:00):");
        String end = ask("끝 시각 (예: 11:00):");
        long id = rooms.reserve(room, user, start, end);
        System.out.printf("%d번 예약: %s회의실 %s~%s (%d분)%n", id, room, start, end, rooms.minutes(start, end));
    }

    private void printSchedule() {
        for (String room : ROOMS) {
            List<String> schedule = rooms.schedule(room);
            System.out.println(room + "회의실: " + (schedule.isEmpty() ? "비어 있음" : String.join(", ", schedule)));
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
