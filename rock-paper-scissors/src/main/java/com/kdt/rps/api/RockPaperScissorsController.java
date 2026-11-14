package com.kdt.rps.api;

public class RockPaperScissorsController implements RockPaperScissorsApi {

    Match match = new Match();

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
        match = new Match();
    }

    //경기 한 판 하기
    @Override
    public String play(String me, String other) {
        return match.play(me, other);
    }

    //승패, 무승부 횟수 출력
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

    ///4단계 : 3선승 경기
    @Override
    public String status() {
        throw new IllegalArgumentException();
//        return rockPaperScissorsService.status();
    }

}
