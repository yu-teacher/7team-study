package com.kdt.library.api;

import java.util.List;

public interface LibraryApi {

    long addBook(String title);

    void join(String member, String type);

    void lend(long bookId, String member);

    void giveBack(long bookId);

    boolean available(long bookId);

    List<Long> loans(String member);

    void today(String date);

    String dueDate(long bookId);

    int unpaidFee(String member);

    void pay(String member, int amount);

    void reserve(long bookId, String member);

    List<String> reservations(long bookId);
}
