package com.kdt.library.api;

import java.util.List;

public class LibraryController implements LibraryApi {

    @Override
    public long addBook(String title) {
        throw new UnsupportedOperationException("1단계");
    }

    @Override
    public void join(String member, String type) {
        throw new UnsupportedOperationException("1단계");
    }

    @Override
    public void lend(long bookId, String member) {
        throw new UnsupportedOperationException("1단계");
    }

    @Override
    public void giveBack(long bookId) {
        throw new UnsupportedOperationException("1단계");
    }

    @Override
    public boolean available(long bookId) {
        throw new UnsupportedOperationException("1단계");
    }

    @Override
    public List<Long> loans(String member) {
        throw new UnsupportedOperationException("1단계");
    }

    @Override
    public void today(String date) {
        throw new UnsupportedOperationException("2단계");
    }

    @Override
    public String dueDate(long bookId) {
        throw new UnsupportedOperationException("2단계");
    }

    @Override
    public int unpaidFee(String member) {
        throw new UnsupportedOperationException("3단계");
    }

    @Override
    public void pay(String member, int amount) {
        throw new UnsupportedOperationException("3단계");
    }

    @Override
    public void reserve(long bookId, String member) {
        throw new UnsupportedOperationException("4단계");
    }

    @Override
    public List<String> reservations(long bookId) {
        throw new UnsupportedOperationException("4단계");
    }
}
