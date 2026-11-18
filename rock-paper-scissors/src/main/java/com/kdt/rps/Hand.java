package com.kdt.rps;

public enum Hand {
    ROCK,
    PAPER,
    SCISSORS;

    public Hand winsAgainst() {
        return switch (this) {
            case ROCK -> SCISSORS;
            case SCISSORS -> PAPER;
            case PAPER -> ROCK;
        };
    }

    // [1] 문자열 Hand 변환 매서드 (호출 시점에 아직 Hand 없음 : static)
    public static Hand from(String name) {
        if (name == null) {
            throw new IllegalArgumentException("손 이름은 null일 수 없습니다.");
        }
        try {
            return Hand.valueOf(name);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("알 수 없는 손입니다: " + name, e);
        }
    }

    // [2] 결과 판정 메서드
    public Result judge(Hand other) {
        if (this == other) {
            return Result.DRAW;
        }
        if (this.winsAgainst() == other) {
            return Result.WIN;
        }
        return Result.LOSE;
    }
}