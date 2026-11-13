package com.kdt.meetingroom.api;

import java.util.List;

public class MeetingRoomController implements MeetingRoomApi {

    @Override
    public int minutes(String start, String end) {
        throw new UnsupportedOperationException("1단계");
    }

    @Override
    public boolean overlaps(String start1, String end1, String start2, String end2) {
        throw new UnsupportedOperationException("1단계");
    }

    @Override
    public long reserve(String room, String member, String start, String end) {
        throw new UnsupportedOperationException("2단계");
    }

    @Override
    public List<String> schedule(String room) {
        throw new UnsupportedOperationException("2단계");
    }

    @Override
    public void now(String time) {
        throw new UnsupportedOperationException("3단계");
    }

    @Override
    public void cancel(long reservationId, String member) {
        throw new UnsupportedOperationException("3단계");
    }

    @Override
    public int usedMinutes(String member) {
        throw new UnsupportedOperationException("4단계");
    }
}
