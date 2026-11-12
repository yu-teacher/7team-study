package com.kdt.board.api;

import java.util.List;

public interface BoardApi {

    long write(String author, String title, String content);

    String title(long id);

    String content(long id);

    String author(long id);

    void edit(long id, String editor, String title, String content);

    void delete(long id, String requester);

    List<Long> list();

    long comment(long postId, String author, String content);

    List<String> comments(long postId);

    int commentCount(long postId);

    void deleteComment(long commentId, String requester);

    List<Long> page(int page);

    List<Long> search(String keyword);

    void view(long id, String viewer);

    int views(long id);
}
