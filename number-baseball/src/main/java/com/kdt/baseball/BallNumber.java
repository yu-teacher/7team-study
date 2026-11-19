package com.kdt.baseball;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class BallNumber {
    private final List<Integer> numbers;

    public BallNumber(List<Integer> numbers) {
        Validator.validateNotNull(numbers);
        validateSize(numbers);
        validateRange(numbers);
        validateNoDuplicate(numbers);

        this.numbers = List.copyOf(numbers);
    }

    public static BallNumber of(String value) {
        Validator.validateNotNull(value);

        if (!value.matches("^[0-9]+$")) {
            throw new IllegalArgumentException("숫자가 아닌 글자가 포함되어서는 안 됩니다.");
        }

        List<Integer> list = new ArrayList<>();
        for (char c : value.toCharArray()) {
            list.add(c - '0');
        }

        return new BallNumber(list);
    }

    public Score judge(BallNumber other) {
        int strike = 0;
        int ball = 0;

        for (int i = 0; i < other.numbers.size(); i++) {
            int num1 = numbers.get(i);
            int num2 = other.numbers.get(i);

            if (num1 == num2) {
                strike++;
            } else if (numbers.contains(num2)) {
                ball++;
            }
        }

        return new Score(strike, ball);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BallNumber other = (BallNumber) o;

        return Objects.equals(numbers, other.numbers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numbers);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int number : numbers) {
            sb.append(number);
        }

        return sb.toString();
    }

    private static void validateSize(List<Integer> numbers) {
        if (numbers.size() != GameRule.SIZE) {
            throw new IllegalArgumentException("숫자의 개수는 " + GameRule.SIZE + "개입니다.");
        }
    }

    private static void validateRange(List<Integer> numbers) {
        for (int number : numbers) {
            if (number < GameRule.MIN_NUMBER || number > GameRule.MAX_NUMBER) {
                throw new IllegalArgumentException("숫자는 " + GameRule.MIN_NUMBER + "~" + GameRule.MAX_NUMBER + " 사이의 정수입니다.");
            }
        }
    }

    private static void validateNoDuplicate(List<Integer> numbers) {
        Set<Integer> set = new HashSet<>();

        for (int number : numbers) {
            set.add(number);
        }

        if (set.size() < numbers.size()) {
            throw new IllegalArgumentException("숫자는 중복되어서는 안 됩니다.");
        }
    }
}