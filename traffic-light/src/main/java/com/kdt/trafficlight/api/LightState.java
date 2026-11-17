package com.kdt.trafficlight.api;

//불변 객체
public class LightState {

    private final Signal signal;
    private final int remaining;

    public LightState(Signal signal, int remaining) {
        this.signal = signal;
        this.remaining = remaining;
    }

    public Signal getSignal(){ return this.signal;}

    public int getRemaining() {return this.remaining;}

    public LightState next(){
        Signal nextSignal = signal.next();
        return new LightState(nextSignal, nextSignal.getSecond());
    }

    public LightState shorten(int limit){
        if(signal != Signal.GREEN || remaining <= limit){
            return this;
        }
        return new LightState(signal, limit);
    }

    public LightState next(int seconds) {
        LightState current = this;
        int rest = seconds;
        while (rest >= current.remaining) {
            rest -= current.remaining;
            current = current.next();
        }
        return new LightState(current.signal, current.remaining - rest);
    }
}
