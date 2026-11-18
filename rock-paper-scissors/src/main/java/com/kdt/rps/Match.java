package com.kdt.rps;

public class Match {

    private static final int FINISH_COUNT = 3;

    private int wins;
    private int losses;
    private int draws;

    public Match() {
        this.wins = 0;
        this.losses = 0;
        this.draws = 0;
    }

    public Result play(Hand me, Hand other) {
        if (status() != MatchStatus.PLAYING) {
            throw new IllegalStateException("이미 종료 된 게임.");
        }

        Result result = me.judge(other);

        switch (result) {
            case WIN -> wins++;
            case LOSE -> losses++;
            case DRAW -> draws++;
        }
        return result;
    }

    public MatchStatus status() {
        if (wins >= FINISH_COUNT) {
            return MatchStatus.WIN;
        }
        if (losses >= FINISH_COUNT) {
            return MatchStatus.LOSE;
        }
        return MatchStatus.PLAYING;
    }

    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }

    public int getDraws() {
        return draws;
    }
}