package com.kdt.rps.api;

public interface RockPaperScissorsApi {

    String winsAgainst(String hand);

    String judge(String me, String other);

    void start();

    String play(String me, String other);

    int wins();

    int losses();

    int draws();

    String status();
}
