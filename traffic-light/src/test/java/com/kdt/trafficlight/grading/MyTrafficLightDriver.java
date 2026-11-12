package com.kdt.trafficlight.grading;

public class MyTrafficLightDriver implements TrafficLightDriver {

    @Override
    public String signal() {
        throw new UnsupportedOperationException("1단계");
    }

    @Override
    public int remainingSeconds() {
        throw new UnsupportedOperationException("2단계");
    }

    @Override
    public String pedestrianSignal() {
        throw new UnsupportedOperationException("3단계");
    }

    @Override
    public void next() {
        throw new UnsupportedOperationException("1단계");
    }

    @Override
    public void elapse(int seconds) {
        throw new UnsupportedOperationException("2단계");
    }

    @Override
    public void pressPedestrianButton() {
        throw new UnsupportedOperationException("3단계");
    }

    @Override
    public void breakDown() {
        throw new UnsupportedOperationException("4단계");
    }

    @Override
    public void repair() {
        throw new UnsupportedOperationException("4단계");
    }
}
