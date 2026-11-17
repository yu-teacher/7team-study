package com.kdt.trafficlight.api;

//도메인 규칙
public enum Signal {
    RED(30),
    GREEN(25),
    YELLOW(5);

    private final int second;

    Signal(int second) {
        this.second = second;
    }

    public int getSecond() {
        return second;
    }

    public Signal next() {
        return switch (this) {
            case RED -> GREEN;
            case GREEN -> YELLOW;
            case YELLOW -> RED;
        };
    }
}

