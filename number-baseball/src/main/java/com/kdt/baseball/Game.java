package com.kdt.baseball;

public class Game {
    private final BallNumber answer;
    private int attempts;
    private boolean solved;

    public Game(BallNumber answer) {
        Validator.validateNotNull(answer);

        this.answer = answer;
        this.attempts = 0;
        this.solved = false;
    }

    public Score guess(BallNumber guessNumber) {
        validateGameStatus();
        Validator.validateNotNull(guessNumber);

        attempts++;

        if (answer.equals(guessNumber)) {
            solved = true;
        }

        return answer.judge(guessNumber);
    }

    public GameStatus status() {
        if (solved) {
            return GameStatus.WIN;
        }
        if (attempts >= GameRule.MAX_ATTEMPTS) {
            return GameStatus.LOSE;
        }
        return GameStatus.PLAYING;
    }

    public int getAttempts() {
        return attempts;
    }

    private void validateGameStatus() {
        if (status() != GameStatus.PLAYING) {
            throw new IllegalStateException("이미 끝난 게임입니다.");
        }
    }
}