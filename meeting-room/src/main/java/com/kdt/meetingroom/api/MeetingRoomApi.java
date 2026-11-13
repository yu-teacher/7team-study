package com.kdt.meetingroom.api;

import java.util.List;

public interface MeetingRoomApi {

    int minutes(String start, String end);

    boolean overlaps(String start1, String end1, String start2, String end2);

    long reserve(String room, String member, String start, String end);

    List<String> schedule(String room);

    void now(String time);

    void cancel(long reservationId, String member);

    int usedMinutes(String member);
}
