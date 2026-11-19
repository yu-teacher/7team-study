package com.kdt.baseball.api;

import com.kdt.baseball.AnswerGenerator;
import com.kdt.baseball.BallNumber;
import com.kdt.baseball.Game;
import com.kdt.baseball.RandomAnswerGenerator;

public class NumberBaseballController implements NumberBaseballApi {

    private final AnswerGenerator answerGenerator = new RandomAnswerGenerator();
    private Game game;

    @Override
    public void start(String answer) {
        this.game = new Game(BallNumber.of(answer));
    }

    @Override
    public String guess(String number) {
        validateStarted();
        return game.guess(BallNumber.of(number)).toString();
    }

    @Override
    public int attempts() {
        validateStarted();
        return game.getAttempts();
    }

    @Override
    public String status() {
        validateStarted();
        return game.status().name();
    }

    @Override
    public String randomAnswer() {
        return answerGenerator.generate().toString();
    }

    private void validateStarted() {
        if (game == null) {
            throw new IllegalStateException("게임을 먼저 시작해 주세요.");
        }
    }
}