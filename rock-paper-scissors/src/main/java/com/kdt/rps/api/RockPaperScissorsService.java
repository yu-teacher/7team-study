package com.kdt.rps.api;

public class RockPaperScissorsService {

    private static final String ROCK = "ROCK";
    private static final String PAPER = "PAPER";
    private static final String SCISSORS = "SCISSORS";

    private int wins;
    private int losses;
    private int draws;
    private String status;

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

    public String judge(String me, String other){
        //무승부
        if(me.equals(other)) return "DRAW";

        //그 외 승패
        if(me.equals(ROCK)){
            if(other.equals(SCISSORS)) return "WIN";
        }
        else if(me.equals(PAPER)){
            if(other.equals(ROCK)) return "WIN";
        }
        else if(me.equals(SCISSORS)){
            if(other.equals(PAPER)) return "WIN";
        }
        return "LOSE";
    }

    public void start() {
        wins = losses = draws = 0;
        status = "PLAYING";
    }

    public void update(String result){
        if(result.equals("DRAW")) draws++;
        else if (result.equals("WIN")) wins++;
        else losses++;
    }

    public int getWins(){
        return this.wins;
    }
    public int getLoses(){
        return this.losses;
    }
    public int getDraws(){
        return this.draws;
    }

    public boolean isFinished(){
        return !status().equals("PLAYING");
    }

    public String status(){
        if(wins>=3) status="WIN";
        else if(losses>=3)status="LOSE";
        else status="PLAYING";
        return status;
    }
}
