package com.kdt.baseball.api;

public class NumberBaseballController implements NumberBaseballApi {

    private final NumberBaseballService  numberBaseballService  = new NumberBaseballService();
    ///1단계 - 숫자규칙
    @Override
    public void start(String answer) {
        if(!numberBaseballService.isRightAnswer(answer)){
            throw new IllegalArgumentException();
        }
        numberBaseballService.setAnswer(answer);
    }

    ///2단계 판정
    @Override
    public String guess(String number) {
        if(!numberBaseballService.isRightAnswer(number)){
            throw new IllegalArgumentException();
        }
        return numberBaseballService.guess(number);
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
