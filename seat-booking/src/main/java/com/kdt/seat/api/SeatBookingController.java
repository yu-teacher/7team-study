package com.kdt.seat.api;

import java.util.List;

public class SeatBookingController implements SeatBookingApi {

    @Override
    public String grade(String seat) {
        throw new UnsupportedOperationException("1단계");
    }

    @Override
    public int price(String seat) {
        throw new UnsupportedOperationException("1단계");
    }

    @Override
    public long book(String customer, List<String> seats) {
        throw new UnsupportedOperationException("2단계");
    }

    @Override
    public int total(long bookingId) {
        throw new UnsupportedOperationException("2단계");
    }

    @Override
    public boolean isSold(String seat) {
        throw new UnsupportedOperationException("2단계");
    }

    @Override
    public List<String> seats(long bookingId) {
        throw new UnsupportedOperationException("2단계");
    }

    @Override
    public int cancel(long bookingId, String customer, int daysLeft) {
        throw new UnsupportedOperationException("3단계");
    }

    @Override
    public int cancelSeat(long bookingId, String customer, String seat, int daysLeft) {
        throw new UnsupportedOperationException("4단계");
    }
}
