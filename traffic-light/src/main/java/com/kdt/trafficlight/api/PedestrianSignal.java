package com.kdt.trafficlight.api;


public enum PedestrianSignal {
    WALK,
    STOP;

    public static PedestrianSignal from(Signal signal) {
        return signal == Signal.RED ? WALK : STOP;
    }
}
