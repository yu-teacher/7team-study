package com.kdt.board.api;

import java.util.List;

public class BoardController implements BoardApi {

    @Override
    public long write(String author, String title, String content) {
        throw new UnsupportedOperationException("1단계");
    }

    @Override
    public String title(long id) {
        throw new UnsupportedOperationException("1단계");
    }

    @Override
    public String content(long id) {
        throw new UnsupportedOperationException("1단계");
    }

    @Override
    public String author(long id) {
        throw new UnsupportedOperationException("1단계");
    }

    @Override
    public void edit(long id, String editor, String title, String content) {
        throw new UnsupportedOperationException("2단계");
    }

    @Override
    public void delete(long id, String requester) {
        throw new UnsupportedOperationException("2단계");
    }

    @Override
    public List<Long> list() {
        throw new UnsupportedOperationException("2단계");
    }

    @Override
    public long comment(long postId, String author, String content) {
        throw new UnsupportedOperationException("3단계");
    }

    @Override
    public List<String> comments(long postId) {
        throw new UnsupportedOperationException("3단계");
    }

    @Override
    public int commentCount(long postId) {
        throw new UnsupportedOperationException("3단계");
    }

    @Override
    public void deleteComment(long commentId, String requester) {
        throw new UnsupportedOperationException("3단계");
    }

    @Override
    public List<Long> page(int page) {
        throw new UnsupportedOperationException("4단계");
    }

    @Override
    public List<Long> search(String keyword) {
        throw new UnsupportedOperationException("4단계");
    }

    @Override
    public void view(long id, String viewer) {
        throw new UnsupportedOperationException("4단계");
    }

    @Override
    public int views(long id) {
        throw new UnsupportedOperationException("4단계");
    }
}
