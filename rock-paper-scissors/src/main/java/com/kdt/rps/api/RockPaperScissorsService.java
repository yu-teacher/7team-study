package com.kdt.rps.api;

public class RockPaperScissorsService {

    private static final String ROCK = "ROCK";
    private static final String PAPER = "PAPER";
    private static final String SCISSORS = "SCISSORS";

    public RockPaperScissorsService rockPaperScissorsService;

    public boolean isRightShape(String hand){
        //손 모양 검증
        //문제해결 - hand가 null이면 오류남. 상수를 앞에 두는 게 좋음
        if(ROCK.equals(hand) ||  PAPER.equals(hand) ||  SCISSORS.equals(hand))
            return true;
        return false;
    }

    public String getAgaintHand(String hand){
        if(hand.equals(ROCK)) return SCISSORS;
        else if(hand.equals(SCISSORS)) return PAPER;
        else return ROCK;
    }
}
