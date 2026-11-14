package com.kdt.rps.api;

public class Match {

    //매서드 작성법 : 행동 = 동사+명사

    ///상태
    String me;
    String other;
    String state = State.PLAYING.name();
    int wins;
    int losses ;
    int draws;

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

        if(m==o) return WinOrLoss.DRAW.name();

        return switch (m){
            case ROCK -> o==Hand.SCISSORS ? WinOrLoss.WIN.name() : WinOrLoss.LOSE.name();
            case PAPER -> o==Hand.ROCK ? WinOrLoss.WIN.name() : WinOrLoss.LOSE.name();
            case SCISSORS -> o==Hand.PAPER ? WinOrLoss.WIN.name() : WinOrLoss.LOSE.name();
        };
    }

    public String play(String me, String other){
        isStillPlaying();
        isRightShape(me);
        isRightShape(other);

        String result = judge(me, other);
        update(result);
        status();
        return result;
    }

    public void update(String result){
        if(WinOrLoss.DRAW.name().equals(result))
            this.draws++;
        else if (WinOrLoss.WIN.name().equals(result))
            this.wins++;
        else this.losses++;
    }

    public int getWins(){
        return this.wins;
    }
    public int getLosses(){
        return this.losses;
    }
    public int getDraws(){
        return this.draws;
    }


    public String status(){
        if(this.wins>=3) this.state = State.WIN.name();
        else if(this.losses>=3) this.state = State.LOSE.name();
        else this.state = State.PLAYING.name();
        return this.state;
    }

    ///규칙
    private void isRightShape(String hand){
    if(!Hand.PAPER.name().equals(hand) && !Hand.SCISSORS.name().equals(hand) && !Hand.ROCK.name().equals(hand)) {
            throw new IllegalArgumentException();
        }
    }

    private void isStillPlaying(){
        if(!State.PLAYING.name().equals(this.state)) {
            throw new IllegalStateException();
        }
    }



}
