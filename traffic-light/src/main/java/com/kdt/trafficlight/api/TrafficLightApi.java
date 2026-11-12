package com.kdt.trafficlight.api;

public interface TrafficLightApi {

    String signal();

    int remainingSeconds();

    String pedestrianSignal();

    void next();

    void elapse(int seconds);

    void pressPedestrianButton();

    void breakDown();

    void repair();
}
