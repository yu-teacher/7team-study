package com.kdt.seat.api;

import java.util.List;

public interface SeatBookingApi {

    String grade(String seat);

    int price(String seat);

    long book(String customer, List<String> seats);

    int total(long bookingId);

    boolean isSold(String seat);

    List<String> seats(long bookingId);

    int cancel(long bookingId, String customer, int daysLeft);

    int cancelSeat(long bookingId, String customer, String seat, int daysLeft);
}
