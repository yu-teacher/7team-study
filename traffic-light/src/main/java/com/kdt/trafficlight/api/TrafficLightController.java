package com.kdt.trafficlight.api;

public class TrafficLightController implements TrafficLightApi {


    ///객체

    //상태 - 현재 신호, 남은 시간, 버튼
    //행동 -
        // start - 빨강으로 초기화
        // next - 신호 바꾸기
        // remainingSeconds - 시간 체크
            // timeCheck - 남은 시간 체크
            // lightCheck - 신호등 체크
        // pedestrianSignal - 보행자 신호 출력
            //pushButton - 버튼 누름(시간 체크)
        // breakDown  - 고장 모드(시간, 신호, 버튼 모두 중지)
            // repair - 수리(초기화)
    //룰 - 0초 이하, 신호가 5초 남았는가
    ///enum
    //신호(색, 시간)
    //보행자 신호(문구)

    private final TrafficLight trafficLight = new TrafficLight();

    @Override
    public String signal() {
        return trafficLight.getSignal();
    }

    @Override
    public int remainingSeconds() {
        return trafficLight.getSecond();
    }

    @Override
    public String pedestrianSignal() {
        return trafficLight.getPedestrian();
    }

    @Override
    public void next() {
        trafficLight.getNext();
    }

    @Override
    public void elapse(int seconds) {
        trafficLight.moveTime(seconds);

    }

    @Override
    public void pressPedestrianButton() {
        trafficLight.pushButton();
    }

    @Override
    public void breakDown() {
        trafficLight.breakDown();
    }

    @Override
    public void repair() {
        trafficLight.repair();
    }
}
