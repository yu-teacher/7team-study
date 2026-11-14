package com.kdt.rps.api;

public class RockPaperScissorsController implements RockPaperScissorsApi {

    private final Match match = new Match();

    //1단계 : 손 모양 판정 및 예외 처리
    @Override
    public String winsAgainst(String hand) {
        return match.winsAgainst(hand);
    }

    //2단계 : 판정(무승부 존재)
    @Override
    public String judge(String me, String other) {
        return match.judge(me,other);
    }

    ///3단계 : 전적(새로시작할 시 돌아감)
    //새 경기 시작
    @Override
    public void start() {
        throw new IllegalArgumentException();
//        rockPaperScissorsService.start();
    }

    //경기 한 판 하기
    @Override
    public String play(String me, String other) {
        throw new IllegalArgumentException();
//        if(rockPaperScissorsService.isFinished()){
//            throw new IllegalStateException();
//        }
//        String result = judge(me, other);
//        rockPaperScissorsService.update(result);
//        return result;
    }

    //승패, 무승부 횟수 출력
    @Override
    public int wins() {
        throw new IllegalArgumentException();
//        return rockPaperScissorsService.getWins();
    }

    @Override
    public int losses() {
        throw new IllegalArgumentException();
//        return rockPaperScissorsService.getLoses();
    }

    @Override
    public int draws() {
        throw new IllegalArgumentException();
//        return rockPaperScissorsService.getDraws();
    }

    ///4단계 : 3선승 경기
    @Override
    public String status() {
        throw new IllegalArgumentException();
//        return rockPaperScissorsService.status();
    }

}
