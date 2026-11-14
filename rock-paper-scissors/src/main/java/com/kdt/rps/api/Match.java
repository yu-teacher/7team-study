package com.kdt.rps.api;

public class Match {

    //매서드 작성법 : 행동 = 동사+명사

    ///상태

    ///행동
    public String winsAgainst(String hand){
        isRightShape(hand);

        // 문자열을 Enum으로 변환하면 반환값이 상수가 됨
        Hand h = Hand.valueOf(hand);
        return switch (h) {
            case PAPER -> "SCISSORS";
            case SCISSORS -> "PAPER";
            default -> "ROCK";
        };
    }

    public String judge(String me, String other){
        isRightShape(me);
        isRightShape(other);

        Hand m = Hand.valueOf(me);
        Hand o = Hand.valueOf(other);

        if(m==o) return "DRAW";

        return switch (m){
            case ROCK -> o==Hand.SCISSORS ? WinOrLoss.WIN.name() : WinOrLoss.LOSE.name();
            case PAPER -> o==Hand.ROCK ? WinOrLoss.WIN.name() : WinOrLoss.LOSE.name();
            case SCISSORS -> o==Hand.PAPER ? WinOrLoss.WIN.name() : WinOrLoss.LOSE.name();
        };
    }
    ///규칙
    private void isRightShape(String hand){
    if(!Hand.PAPER.name().equals(hand) && !Hand.SCISSORS.name().equals(hand) && !Hand.ROCK.name().equals(hand)) {
            throw new IllegalArgumentException();
        }
    }

}
