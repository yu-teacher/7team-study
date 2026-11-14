package com.kdt.baseball.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class NumberBaseballService {

    private String answer;

    public boolean isRightAnswer(String answer){
        //false 조건
        if(answer.isBlank() || answer.length()!=3) return false;
        for(char c : answer.toCharArray()){
            if(c=='0' || !Character.isDigit(c)) return false;
        }
        if(answer.chars().distinct().count()!=3)
            return false;

        //모두 통과 시 true
        return true;
    }

    //정답 저장
    public void setAnswer(String answer){
        this.answer = answer;
    }

    //판정
    public String guess(String number) {

        char[] list1 = answer.toCharArray();
        char[] list2 = number.toCharArray();
        StringBuffer sb = new StringBuffer();

        int ball = 0;
        int strike = 0;
        for (int i = 0; i < 3; i++) {
            if (list1[i] == list2[i])
                ball++;
        }
        strike = 3 - ball;

        if (ball == 0 && strike == 0) {
            return "낫싱";
        }

        if (strike > 0) {
            sb.append(strike).append("스트라이크");
        }
        if (ball > 0) {
            if(sb.length()>0) sb.append(" ");
            sb.append(ball).append("볼");
        }

        return sb.toString();
    }
}
