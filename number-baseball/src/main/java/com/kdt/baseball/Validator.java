package com.kdt.baseball;

final class Validator {

    private Validator() {
    }

    static void validateNotNull(Object value) {
        if (value == null) {
            throw new IllegalArgumentException("값은 null일 수 없습니다.");
        }
    }
}