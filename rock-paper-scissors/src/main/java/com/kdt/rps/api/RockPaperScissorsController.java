package com.kdt.rps.api;

public class RockPaperScissorsController implements RockPaperScissorsApi {

    private final RockPaperScissorsService rockPaperScissorsService = new RockPaperScissorsService() ;

    //1단계 : 손 모양 판정 및 예외 처리
    @Override
    public String winsAgainst(String hand) {
        if(!rockPaperScissorsService.isRightShape(hand)){
            throw new IllegalArgumentException();
        }
        return rockPaperScissorsService.getAgaintHand(hand);
    }

    @Override
    public String judge(String me, String other) {
        throw new UnsupportedOperationException("2단계");
    }

    @Override
    public void start() {
        throw new UnsupportedOperationException("3단계");
    }

    @Override
    public String play(String me, String other) {
        throw new UnsupportedOperationException("3단계");
    }

    @Override
    public int wins() {
        throw new UnsupportedOperationException("3단계");
    }

    @Override
    public int losses() {
        throw new UnsupportedOperationException("3단계");
    }

    @Override
    public int draws() {
        throw new UnsupportedOperationException("3단계");
    }

    @Override
    public String status() {
        throw new UnsupportedOperationException("4단계");
    }
}
