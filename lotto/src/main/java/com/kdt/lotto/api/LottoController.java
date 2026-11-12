package com.kdt.lotto.api;

import java.util.List;

public class LottoController implements LottoApi {

    @Override
    public List<Integer> ticket(List<Integer> numbers) {
        throw new UnsupportedOperationException("1단계");
    }

    @Override
    public void winning(List<Integer> numbers, int bonus) {
        throw new UnsupportedOperationException("2단계");
    }

    @Override
    public String rank(List<Integer> numbers) {
        throw new UnsupportedOperationException("3단계");
    }

    @Override
    public long prize(List<Integer> numbers) {
        throw new UnsupportedOperationException("3단계");
    }

    @Override
    public List<List<Integer>> buyAuto(int money) {
        throw new UnsupportedOperationException("4단계");
    }

    @Override
    public double profitRate(List<List<Integer>> tickets) {
        throw new UnsupportedOperationException("4단계");
    }
}
