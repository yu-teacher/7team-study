package com.kdt.baseball.grading;

public interface NumberBaseballDriver {

    void start(String answer);

    String guess(String number);

    int attempts();

    String status();

    String randomAnswer();
}
