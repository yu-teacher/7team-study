package com.kdt.lotto.api;

import java.util.List;

public interface LottoApi {

    List<Integer> ticket(List<Integer> numbers);

    void winning(List<Integer> numbers, int bonus);

    String rank(List<Integer> numbers);

    long prize(List<Integer> numbers);

    List<List<Integer>> buyAuto(int money);

    double profitRate(List<List<Integer>> tickets);
}
