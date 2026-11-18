package com.kdt.rps.api;

import com.kdt.rps.Hand;
import com.kdt.rps.Match;

public class RockPaperScissorsController implements RockPaperScissorsApi {

    private Match match;

    public RockPaperScissorsController() {
        this.match = new Match();
    }

    @Override
    public String winsAgainst(String hand) {
        return Hand.from(hand).winsAgainst().name();
    }

    @Override
    public String judge(String me, String other) {
        return Hand.from(me).judge(Hand.from(other)).name();
    }

    @Override
    public void start() {
        this.match = new Match();
    }

    @Override
    public String play(String me, String other) {
        Hand myHand = Hand.from(me);
        Hand otherHand = Hand.from(other);
        return match.play(myHand, otherHand).name();
    }

    @Override
    public int wins() {
        return match.getWins();
    }

    @Override
    public int losses() {
        return match.getLosses();
    }

    @Override
    public int draws() {
        return match.getDraws();
    }

    @Override
    public String status() {
        return match.status().name();
    }
}