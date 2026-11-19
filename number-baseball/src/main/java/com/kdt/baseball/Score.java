package com.kdt.baseball;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Score {
    private final int strike;
    private final int ball;

    public Score(int strike, int ball) {
        validateNotNegative(strike, ball);
        validateTotal(strike, ball);
        validatePossible(strike, ball);

        this.strike = strike;
        this.ball = ball;
    }

    @Override
    public String toString() {
        List<String> scoreList = new ArrayList<>();

        if (strike > 0) {
            scoreList.add(strike + "스트라이크");
        }
        if (ball > 0) {
            scoreList.add(ball + "볼");
        }

        if (scoreList.isEmpty()) {
            return "낫싱";
        }

        return String.join(" ", scoreList);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Score other = (Score) o;

        return strike == other.strike && ball == other.ball;
    }

    @Override
    public int hashCode() {
        return Objects.hash(strike, ball);
    }

    private static void validateNotNegative(int strike, int ball) {
        if (strike < 0 || ball < 0) {
            throw new IllegalArgumentException("스트라이크와 볼은 0 이상이어야 합니다.");
        }
    }

    private static void validateTotal(int strike, int ball) {
        if (strike + ball > GameRule.SIZE) {
            throw new IllegalArgumentException("스트라이크와 볼의 합은 자릿수 이하여야 합니다.");
        }
    }

    private static void validatePossible(int strike, int ball) {
        if (strike == GameRule.SIZE - 1 && ball == 1) {
            throw new IllegalArgumentException("스트라이크가 " + strike + "개이면 볼은 나올 수 없습니다.");
        }
    }
}