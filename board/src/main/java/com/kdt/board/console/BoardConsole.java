package com.kdt.board.console;

import com.kdt.board.api.BoardApi;
import com.kdt.board.api.BoardController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * 직접 써 보는 콘솔입니다. 채점하지 않으며 수정할 필요도 없습니다.
 * 채점 테스트와 똑같이 BoardApi(컨트롤러)만 부릅니다.
 */
public class BoardConsole {
    private static final String MENU = """

            [%s님] 1.글쓰기 2.목록 3.글보기 4.수정 5.삭제 6.댓글 7.댓글삭제 8.검색 9.사용자 바꾸기 0.끝내기""";

    private final BoardApi board = new BoardController();
    private final Scanner scanner = new Scanner(System.in);
    // 화면에 "1번째 댓글"처럼 보여 주기 위해 콘솔이 댓글 번호를 기억해 둡니다.
    private final Map<Long, List<Long>> commentIds = new HashMap<>();
    private String user;

    public static void main(String[] args) {
        new BoardConsole().run();
    }

    private void run() {
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
                case "1" -> write();
                case "2" -> printPosts(board.page(Integer.parseInt(ask("몇 페이지를 볼까요?"))));
                case "3" -> read(id("글 번호:"));
                case "4" -> edit();
                case "5" -> delete();
                case "6" -> comment();
                case "7" -> deleteComment();
                case "8" -> printPosts(board.search(ask("제목 검색어:")));
                case "9" -> user = ask("이름을 입력하세요:");
                default -> System.out.println("메뉴 번호를 입력해 주세요.");
            }
        } catch (UnsupportedOperationException e) {
            System.out.println("아직 구현하지 않은 기능입니다 (" + e.getMessage() + ").");
        } catch (IllegalArgumentException e) {
            System.out.println("입력이 잘못되었습니다: " + e.getMessage());
        } catch (IllegalStateException e) {
            System.out.println("권한이 없습니다: " + e.getMessage());
        } catch (NoSuchElementException e) {
            System.out.println("찾을 수 없습니다: " + e.getMessage());
        }
    }

    private void write() {
        long id = board.write(user, ask("제목:"), ask("본문:"));
        System.out.println(id + "번 글을 썼습니다.");
    }

    private void read(long id) {
        board.view(id, user);
        System.out.printf("%n[%d] %s%n작성자: %s | 조회수: %d%n%s%n", id, board.title(id), board.author(id), board.views(id), board.content(id));
        List<String> comments = board.comments(id);
        System.out.printf("댓글 %d개%n", board.commentCount(id));
        for (int i = 0; i < comments.size(); i++) {
            System.out.printf("  %d. %s%n", i + 1, comments.get(i));
        }
    }

    private void edit() {
        long id = id("수정할 글 번호:");
        board.edit(id, user, ask("새 제목:"), ask("새 본문:"));
        System.out.println("수정했습니다.");
    }

    private void delete() {
        long id = id("삭제할 글 번호:");
        board.delete(id, user);
        commentIds.remove(id);
        System.out.println("삭제했습니다.");
    }

    private void comment() {
        long postId = id("댓글을 달 글 번호:");
        long commentId = board.comment(postId, user, ask("댓글:"));
        commentIds.computeIfAbsent(postId, key -> new ArrayList<>()).add(commentId);
        System.out.println("댓글을 달았습니다.");
    }

    private void deleteComment() {
        long postId = id("글 번호:");
        List<Long> ids = commentIds.getOrDefault(postId, List.of());
        int index = Integer.parseInt(ask("몇 번째 댓글을 지울까요?")) - 1;
        if (index < 0 || index >= ids.size()) {
            throw new NoSuchElementException("그런 댓글이 없습니다");
        }
        board.deleteComment(ids.get(index), user);
        ids.remove(index);
        System.out.println("댓글을 삭제했습니다.");
    }

    private void printPosts(List<Long> ids) {
        if (ids.isEmpty()) {
            System.out.println("글이 없습니다.");
            return;
        }
        System.out.println("번호 | 제목 | 작성자 | 댓글 | 조회수");
        for (long id : ids) {
            System.out.printf("%d | %s | %s | %d | %d%n", id, board.title(id), board.author(id), board.commentCount(id), board.views(id));
        }
    }

    private long id(String message) {
        return Long.parseLong(ask(message));
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
