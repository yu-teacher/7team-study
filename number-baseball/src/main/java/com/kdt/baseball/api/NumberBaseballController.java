package com.kdt.baseball.api;

public class NumberBaseballController implements NumberBaseballApi {

    ///객체 - Match
    //행동 - ...
    //룰 - 세자리, 0이 있는가, 같은 숫자가 없는가, 숫자 이외 것이 있는가.
    ///enum
    //판정 - 스트라이크, 불, 낫싱
    //상태 - 승, 패, 진행중

    private final Match match = new Match();

    @Override
    public void start(String answer) {
        match.start();
        match.saveNumber(answer);
    }

    @Override
    public String guess(String number) {

        return match.guess(number);
    }

    @Override
    public int attempts() {
        return match.attempts();
    }

    @Override
    public String status() {
        return match.statue();
    }

    @Override
    public String randomAnswer() {
        return match.randomAnswer();
    }
}
