package com.kdt.trafficlight.grading;

public interface TrafficLightDriver {

    String signal();

    int remainingSeconds();

    String pedestrianSignal();

    void next();

    void elapse(int seconds);

    void pressPedestrianButton();

    void breakDown();

    void repair();
}
