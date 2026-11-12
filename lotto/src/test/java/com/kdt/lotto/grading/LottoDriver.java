package com.kdt.lotto.grading;

import java.util.List;

public interface LottoDriver {

    List<Integer> ticket(List<Integer> numbers);

    void winning(List<Integer> numbers, int bonus);

    String rank(List<Integer> numbers);

    long prize(List<Integer> numbers);

    List<List<Integer>> buyAuto(int money);

    double profitRate(List<List<Integer>> tickets);
}
