package com.kdt.board.grading;

import com.kdt.board.api.BoardApi;
import com.kdt.board.api.BoardController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("게시판 채점")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
class BoardGradingTest {
    private static final String AUTHOR = "철수";
    private static final String OTHER = "영희";
    private static final String STRANGER = "민수";

    private BoardApi board;

    @BeforeEach
    void setUp() {
        board = new BoardController();
    }

    private long write() {
        return board.write(AUTHOR, "제목", "본문");
    }

    private void writeMany(int count) {
        for (int i = 0; i < count; i++) {
            write();
        }
    }

    @Nested
    @Order(1)
    @DisplayName("1단계: 글 쓰기와 조회")
    class Step1 {

        @Test
        @DisplayName("1-1. 글 번호는 1부터 하나씩 늘어난다")
        void idsIncrease() {
            assertThat(write()).isEqualTo(1L);
            assertThat(write()).isEqualTo(2L);
            assertThat(write()).isEqualTo(3L);
        }

        @Test
        @DisplayName("1-2. 쓴 글의 작성자, 제목, 본문을 조회할 수 있다")
        void readsPost() {
            long id = board.write(AUTHOR, "안녕하세요", "첫 글입니다");

            assertThat(board.author(id)).isEqualTo(AUTHOR);
            assertThat(board.title(id)).isEqualTo("안녕하세요");
            assertThat(board.content(id)).isEqualTo("첫 글입니다");
        }

        @Test
        @DisplayName("1-3. 제목은 1자와 50자까지 허용")
        void acceptsTitleLength() {
            long one = board.write(AUTHOR, "가", "본문");
            long fifty = board.write(AUTHOR, "가".repeat(50), "본문");

            assertThat(board.title(one)).isEqualTo("가");
            assertThat(board.title(fifty)).hasSize(50);
        }

        @ParameterizedTest(name = "1-4. 제목 \"{0}\"은 IllegalArgumentException")
        @ValueSource(strings = {"", " ", "   "})
        void rejectsBlankTitle(String title) {
            assertThatThrownBy(() -> board.write(AUTHOR, title, "본문"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("1-5. 제목이 51자면 IllegalArgumentException")
        void rejectsLongTitle() {
            assertThatThrownBy(() -> board.write(AUTHOR, "가".repeat(51), "본문"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @ParameterizedTest(name = "1-6. 본문 \"{0}\"은 IllegalArgumentException")
        @ValueSource(strings = {"", " ", "   "})
        void rejectsBlankContent(String content) {
            assertThatThrownBy(() -> board.write(AUTHOR, "제목", content))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @ParameterizedTest(name = "1-7. 작성자 \"{0}\"은 IllegalArgumentException")
        @ValueSource(strings = {"", " "})
        void rejectsBlankAuthor(String author) {
            assertThatThrownBy(() -> board.write(author, "제목", "본문"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("1-8. 규칙에 맞지 않는 글은 번호를 쓰지 않는다")
        void invalidPostDoesNotUseId() {
            write();
            assertThatThrownBy(() -> board.write(AUTHOR, "", "본문"))
                    .isInstanceOf(IllegalArgumentException.class);

            assertThat(write()).isEqualTo(2L);
        }

        @Test
        @DisplayName("1-9. 없는 번호를 조회하면 NoSuchElementException")
        void missingPost() {
            write();

            assertThatThrownBy(() -> board.title(99))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }

    @Nested
    @Order(2)
    @DisplayName("2단계: 수정과 삭제")
    class Step2 {

        @Test
        @DisplayName("2-1. 작성자가 수정하면 제목과 본문이 바뀌고 작성자는 그대로")
        void authorEdits() {
            long id = write();
            board.edit(id, AUTHOR, "새 제목", "새 본문");

            assertThat(board.title(id)).isEqualTo("새 제목");
            assertThat(board.content(id)).isEqualTo("새 본문");
            assertThat(board.author(id)).isEqualTo(AUTHOR);
        }

        @Test
        @DisplayName("2-2. 다른 사람이 수정하면 IllegalStateException, 글은 그대로")
        void otherCannotEdit() {
            long id = write();

            assertThatThrownBy(() -> board.edit(id, OTHER, "새 제목", "새 본문"))
                    .isInstanceOf(IllegalStateException.class);
            assertThat(board.title(id)).isEqualTo("제목");
            assertThat(board.content(id)).isEqualTo("본문");
        }

        @Test
        @DisplayName("2-3. 수정할 때도 1단계 규칙을 지킨다, 어기면 글은 그대로")
        void editFollowsRules() {
            long id = write();

            assertThatThrownBy(() -> board.edit(id, AUTHOR, "가".repeat(51), "새 본문"))
                    .isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> board.edit(id, AUTHOR, "새 제목", " "))
                    .isInstanceOf(IllegalArgumentException.class);
            assertThat(board.title(id)).isEqualTo("제목");
            assertThat(board.content(id)).isEqualTo("본문");
        }

        @Test
        @DisplayName("2-4. 작성자가 삭제하면 조회할 때 NoSuchElementException")
        void authorDeletes() {
            long id = write();
            board.delete(id, AUTHOR);

            assertThatThrownBy(() -> board.title(id))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("2-5. 다른 사람이 삭제하면 IllegalStateException, 글은 남아 있다")
        void otherCannotDelete() {
            long id = write();

            assertThatThrownBy(() -> board.delete(id, OTHER))
                    .isInstanceOf(IllegalStateException.class);
            assertThat(board.title(id)).isEqualTo("제목");
        }

        @Test
        @DisplayName("2-6. 삭제된 글을 수정하거나 삭제하면 NoSuchElementException")
        void deletedPostIsGone() {
            long id = write();
            board.delete(id, AUTHOR);

            assertThatThrownBy(() -> board.edit(id, AUTHOR, "새 제목", "새 본문"))
                    .isInstanceOf(NoSuchElementException.class);
            assertThatThrownBy(() -> board.delete(id, AUTHOR))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("2-7. 삭제된 번호는 다시 쓰지 않는다")
        void deletedIdNotReused() {
            write();
            long second = write();
            board.delete(second, AUTHOR);

            assertThat(write()).isEqualTo(3L);
        }

        @Test
        @DisplayName("2-8. 목록은 최신 글부터, 삭제된 글은 빠진다")
        void listNewestFirst() {
            writeMany(3);
            board.delete(2, AUTHOR);

            assertThat(board.list()).containsExactly(3L, 1L);
        }

        @Test
        @DisplayName("2-9. 글이 없으면 빈 목록")
        void emptyList() {
            assertThat(board.list()).isEmpty();
        }
    }

    @Nested
    @Order(3)
    @DisplayName("3단계: 댓글")
    class Step3 {

        @Test
        @DisplayName("3-1. 댓글 번호는 1부터, 글과 상관없이 하나씩 늘어난다")
        void commentIdsIncrease() {
            long first = write();
            long second = write();

            assertThat(board.comment(first, OTHER, "좋아요")).isEqualTo(1L);
            assertThat(board.comment(second, OTHER, "좋아요")).isEqualTo(2L);
            assertThat(board.comment(first, OTHER, "좋아요")).isEqualTo(3L);
        }

        @Test
        @DisplayName("3-2. 댓글은 먼저 단 것부터 보이고, 댓글 수를 센다")
        void commentsOldestFirst() {
            long id = write();
            board.comment(id, OTHER, "첫 댓글");
            board.comment(id, STRANGER, "두 번째 댓글");

            assertThat(board.comments(id)).containsExactly("첫 댓글", "두 번째 댓글");
            assertThat(board.commentCount(id)).isEqualTo(2);
        }

        @Test
        @DisplayName("3-3. 댓글이 없으면 빈 목록, 0개")
        void noComments() {
            long id = write();

            assertThat(board.comments(id)).isEmpty();
            assertThat(board.commentCount(id)).isZero();
        }

        @Test
        @DisplayName("3-4. 댓글 내용이나 작성자가 비어 있으면 IllegalArgumentException, 댓글 수 그대로")
        void rejectsBlankComment() {
            long id = write();

            assertThatThrownBy(() -> board.comment(id, OTHER, " "))
                    .isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> board.comment(id, "", "좋아요"))
                    .isInstanceOf(IllegalArgumentException.class);
            assertThat(board.commentCount(id)).isZero();
        }

        @Test
        @DisplayName("3-5. 없거나 삭제된 글에 댓글을 달면 NoSuchElementException")
        void commentOnMissingPost() {
            long id = write();
            board.delete(id, AUTHOR);

            assertThatThrownBy(() -> board.comment(id, OTHER, "좋아요"))
                    .isInstanceOf(NoSuchElementException.class);
            assertThatThrownBy(() -> board.comment(99, OTHER, "좋아요"))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("3-6. 댓글 작성자는 자기 댓글을 삭제할 수 있다")
        void commenterDeletes() {
            long id = write();
            long commentId = board.comment(id, OTHER, "좋아요");
            board.deleteComment(commentId, OTHER);

            assertThat(board.comments(id)).isEmpty();
        }

        @Test
        @DisplayName("3-7. 글 작성자는 남의 댓글도 삭제할 수 있다")
        void postAuthorDeletes() {
            long id = write();
            long commentId = board.comment(id, OTHER, "좋아요");
            board.deleteComment(commentId, AUTHOR);

            assertThat(board.commentCount(id)).isZero();
        }

        @Test
        @DisplayName("3-8. 그 밖의 사람이 삭제하면 IllegalStateException, 댓글은 남아 있다")
        void strangerCannotDelete() {
            long id = write();
            long commentId = board.comment(id, OTHER, "좋아요");

            assertThatThrownBy(() -> board.deleteComment(commentId, STRANGER))
                    .isInstanceOf(IllegalStateException.class);
            assertThat(board.comments(id)).containsExactly("좋아요");
        }

        @Test
        @DisplayName("3-9. 없거나 이미 삭제된 댓글을 삭제하면 NoSuchElementException")
        void deleteMissingComment() {
            long id = write();
            long commentId = board.comment(id, OTHER, "좋아요");
            board.deleteComment(commentId, OTHER);

            assertThatThrownBy(() -> board.deleteComment(commentId, OTHER))
                    .isInstanceOf(NoSuchElementException.class);
            assertThatThrownBy(() -> board.deleteComment(99, OTHER))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("3-10. 글을 삭제하면 댓글도 함께 사라진다")
        void deletingPostRemovesComments() {
            long id = write();
            long commentId = board.comment(id, OTHER, "좋아요");
            board.delete(id, AUTHOR);

            assertThatThrownBy(() -> board.comments(id))
                    .isInstanceOf(NoSuchElementException.class);
            assertThatThrownBy(() -> board.deleteComment(commentId, OTHER))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }

    @Nested
    @Order(4)
    @DisplayName("4단계: 목록 페이지, 검색, 조회수")
    class Step4 {

        @Test
        @DisplayName("4-1. 한 페이지에 10개씩, 최신 글부터")
        void pages() {
            writeMany(25);

            assertThat(board.page(1)).containsExactly(25L, 24L, 23L, 22L, 21L, 20L, 19L, 18L, 17L, 16L);
            assertThat(board.page(2)).containsExactly(15L, 14L, 13L, 12L, 11L, 10L, 9L, 8L, 7L, 6L);
            assertThat(board.page(3)).containsExactly(5L, 4L, 3L, 2L, 1L);
        }

        @Test
        @DisplayName("4-2. 글이 없는 페이지는 빈 목록")
        void emptyPage() {
            writeMany(25);

            assertThat(board.page(4)).isEmpty();
        }

        @ParameterizedTest(name = "4-3. 페이지 {0}은 IllegalArgumentException")
        @ValueSource(ints = {0, -1})
        void rejectsInvalidPage(int page) {
            assertThatThrownBy(() -> board.page(page))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("4-4. 삭제된 글은 페이지에서 빠지고 뒤의 글이 당겨진다")
        void deletedPostsShiftPages() {
            writeMany(11);
            board.delete(11, AUTHOR);

            assertThat(board.page(1)).containsExactly(10L, 9L, 8L, 7L, 6L, 5L, 4L, 3L, 2L, 1L);
            assertThat(board.page(2)).isEmpty();
        }

        @Test
        @DisplayName("4-5. 제목에 검색어가 들어간 글을 최신 글부터 찾는다")
        void searchesTitle() {
            board.write(AUTHOR, "자바 공부", "본문");
            board.write(AUTHOR, "스프링 입문", "자바 이야기");
            board.write(AUTHOR, "자바 스터디 모집", "본문");
            board.write(AUTHOR, "Java", "본문");

            assertThat(board.search("자바")).containsExactly(3L, 1L);
            assertThat(board.search("스터디")).containsExactly(3L);
        }

        @Test
        @DisplayName("4-6. 검색은 대소문자를 구분하고, 없으면 빈 목록")
        void searchIsCaseSensitive() {
            board.write(AUTHOR, "Java", "본문");

            assertThat(board.search("java")).isEmpty();
            assertThat(board.search("없음")).isEmpty();
        }

        @Test
        @DisplayName("4-7. 삭제된 글은 검색되지 않는다")
        void searchSkipsDeleted() {
            board.write(AUTHOR, "자바 공부", "본문");
            board.write(AUTHOR, "자바 질문", "본문");
            board.delete(2, AUTHOR);

            assertThat(board.search("자바")).containsExactly(1L);
        }

        @ParameterizedTest(name = "4-8. 검색어 \"{0}\"은 IllegalArgumentException")
        @ValueSource(strings = {"", " "})
        void rejectsBlankKeyword(String keyword) {
            assertThatThrownBy(() -> board.search(keyword))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("4-9. 새 글의 조회수는 0, 다른 사람이 볼 때마다 1씩 는다")
        void viewsCount() {
            long id = write();
            assertThat(board.views(id)).isZero();

            board.view(id, OTHER);
            board.view(id, OTHER);
            board.view(id, STRANGER);

            assertThat(board.views(id)).isEqualTo(3);
        }

        @Test
        @DisplayName("4-10. 작성자가 자기 글을 보면 조회수가 늘지 않는다")
        void authorViewDoesNotCount() {
            long id = write();
            board.view(id, AUTHOR);

            assertThat(board.views(id)).isZero();
        }

        @Test
        @DisplayName("4-11. 없거나 삭제된 글을 보면 NoSuchElementException")
        void viewMissingPost() {
            long id = write();
            board.delete(id, AUTHOR);

            assertThatThrownBy(() -> board.view(id, OTHER))
                    .isInstanceOf(NoSuchElementException.class);
            assertThatThrownBy(() -> board.views(99))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }
}
