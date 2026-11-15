package com.kdt.baseball.api;

public enum Call {
    STRIKE("스트라이크"),
    BALL("볼"),
    NOTHING("낫싱");

    private final String label;

    Call(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}
