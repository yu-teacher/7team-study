package com.kdt.trafficlight.api;

public class TrafficLight {

    //클래스 나누기
    //

    ///상태
    private static final int BUTTON_LIMIT_SECONDS = 5;
    private static final String FLASHING_YELLOW = "FLASHING_YELLOW";

    //초기화
    private LightState state;
    private boolean broken;

    public TrafficLight(){
        //이 과제에선 효율적인가?
        //불변 객체 사용하는 이유
            //여러번 호출하기 떄문.
        state = start();
        broken = false;
    }

    private LightState start(){
        return new LightState(Signal.RED, Signal.RED.getSecond());
    }

    ///행동
    //출력, 변동, 저장, 검증

    public String getSignal(){
        if (isBroken()) {
            return FLASHING_YELLOW;
        }
        return state.getSignal().name();
    }

    public int getSecond(){
        if (isBroken()) {
            return 0;
        }
        return state.getRemaining();
    }

    public String getPedestrian() {
        if (isBroken()) {
            return PedestrianSignal.STOP.name();
        }
        return PedestrianSignal.from(state.getSignal()).name();
    }

    // 명령
    public void getNext() {
        if (isBroken()) {
            return;
        }
        save(state.next());
    }

    public void moveTime(int seconds){
        if (isBroken()) {
            return;
        }
        valid(seconds);
        save(state.next(seconds));
    }

    public void pushButton(){
        if(isBroken()){
            return;
        }
        save(state.shorten(BUTTON_LIMIT_SECONDS));
    }

    public void breakDown() {
        broken = true;
    }

    public void repair() {
        if (!isBroken()) {
            return;
        }
        broken = false;
        save(start());
    }

    ///룰
    private void isNotSmallThenZero(int seconds){
        if(seconds<=0){
            throw new IllegalArgumentException();
        }
    }

    private boolean isBroken() {
        return broken;
    }

    private void save(LightState state){
        this.state = state;
    }

    private void valid(int seconds){
        if(seconds <= 0){
            throw new IllegalArgumentException("seconds must be positive: " + seconds);
        }
    }
}
