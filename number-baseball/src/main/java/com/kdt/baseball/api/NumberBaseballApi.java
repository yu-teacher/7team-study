package com.kdt.baseball.api;

public interface NumberBaseballApi {

    void start(String answer);

    String guess(String number);

    int attempts();

    String status();

    String randomAnswer();
}
