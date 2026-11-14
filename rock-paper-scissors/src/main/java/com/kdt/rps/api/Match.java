package com.kdt.rps.api;

public class Match {

    //매서드 작성법 : 행동 = 동사+명사

    ///상태

    ///행동
    public String winsAgainst(String hand){
        isRightShape(hand);
        if(Hand.ROCK.name().equals(hand)) return  "SCISSORS";
        else if(Hand.SCISSORS.name().equals(hand)) return  "PAPER";
        else return  "ROCK";
    }

    ///규칙
    private void isRightShape(String hand){
    if(!Hand.PAPER.name().equals(hand) && !Hand.SCISSORS.name().equals(hand) && !Hand.ROCK.name().equals(hand)) {
            throw new IllegalArgumentException();
        }
    }

}
