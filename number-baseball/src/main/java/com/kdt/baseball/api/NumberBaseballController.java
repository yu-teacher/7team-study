package com.kdt.baseball.api;

public class NumberBaseballController implements NumberBaseballApi {

    @Override
    public void start(String answer) {
        throw new UnsupportedOperationException("1단계");
    }

    @Override
    public String guess(String number) {
        throw new UnsupportedOperationException("2단계");
    }

    @Override
    public int attempts() {
        throw new UnsupportedOperationException("3단계");
    }

    @Override
    public String status() {
        throw new UnsupportedOperationException("3단계");
    }

    @Override
    public String randomAnswer() {
        throw new UnsupportedOperationException("4단계");
    }
}
