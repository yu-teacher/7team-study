package com.kdt.baseball;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RandomAnswerGenerator implements AnswerGenerator {
    private final Random random = new Random();

    @Override
    public BallNumber generate() {
        List<Integer> numbers = new ArrayList<>();

        for (int i = GameRule.MIN_NUMBER; i <= GameRule.MAX_NUMBER; i++) {
            numbers.add(i);
        }

        Collections.shuffle(numbers, random);
        List<Integer> answerNumbers = numbers.subList(0, GameRule.SIZE);

        return new BallNumber(answerNumbers);
    }
}