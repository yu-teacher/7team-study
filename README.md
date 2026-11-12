# 7팀 스터디

기능이나 주제를 **순수 자바**로 구현해 보고, 서로의 설계를 비교하는 스터디입니다.

## 문제 목록

위에서부터 순서대로 푸는 것을 추천합니다. 처음이라면 **가위바위보**부터 시작하세요.

| 순서 | 문제 | 폴더 | 난이도 | 주요 연습 포인트 |
|---|---|---|---|---|
| 1 | 가위바위보 | [rock-paper-scissors](rock-paper-scissors) | 하- | enum에 규칙 넣기, 판정 결과, 간단한 경기 상태 |
| 2 | 숫자 야구 | [number-baseball](number-baseball) | 하 | 값 객체, 검증 위치, 무작위 값 분리 |
| 3 | 신호등 | [traffic-light](traffic-light) | 중하 | enum, 상태 전이, 시간 흐름 처리 |
| 4 | 로또 | [lotto](lotto) | 중 | 일급 컬렉션, 등수 enum, 금액 계산과 반올림 |
| 5 | 게시판 | [board](board) | 중 | 객체 사이의 관계, 저장소, 권한 확인 위치 |

채점 테스트와 컨트롤러가 어떻게 동작하는지는 [가위바위보 README의 채점하기](rock-paper-scissors/README.md#채점하기)에 자세히 적어 두었습니다.

## 구조

```
7팀-스터디/
├── build.gradle        공통 설정 (Java 21, JUnit 5, AssertJ)
├── settings.gradle     src 폴더가 있는 하위 폴더를 자동으로 포함
├── rock-paper-scissors/
│   ├── README.md                         요구사항
│   └── src/
│       ├── main/java/com/kdt/rps/
│       │   ├── api/RockPaperScissorsApi.java          채점과 콘솔이 부르는 약속 (고정)
│       │   ├── api/RockPaperScissorsController.java   내 코드로 넘기는 컨트롤러 (여기만 채움)
│       │   ├── console/RockPaperScissorsConsole.java  직접 실행해 보는 콘솔 (채점 안 함)
│       │   └── (내 도메인 코드)                       설계 자유
│       └── test/java/com/kdt/rps/grading/
│           └── RockPaperScissorsGradingTest.java      채점 테스트 (고정)
└── ...
```

- 저장소 루트를 IntelliJ로 열면 모든 문제가 한 번에 보입니다.
- 문제 폴더에는 `build.gradle`이 없습니다. 루트 설정을 함께 씁니다.

## 진행 방법

1. `main` 브랜치에서 새 브랜치를 만듭니다. 이름은 `문제/이름` 형식입니다.
   ```bash
   git switch -c rock-paper-scissors/홍길동
   ```
2. 해당 문제 폴더의 README를 읽고 구현합니다. 테스트는 따로 작성하지 않고, 문제마다 들어 있는 **채점 테스트**로 확인합니다.
3. 자기 브랜치에 푸시하고 `main`으로 PR을 엽니다. 서로 PR에 코드 리뷰를 남깁니다.
4. 스터디 때 README의 "모여서 이야기할 거리"로 설계를 비교합니다.

`main`은 문제(README와 빈 틀)만 두는 곳이라 PR은 병합하지 않습니다.

## 실행

채점 테스트:

```bash
./gradlew :rock-paper-scissors:test
```

직접 실행해 보기 (문제마다 콘솔이 미리 들어 있고, 채점하지 않습니다):

```bash
./gradlew -q --console=plain :rock-paper-scissors:console
```

IntelliJ에서는 이렇게 실행합니다.

1. 각 문제의 `src/main/java/com/kdt/<패키지>/console/XxxConsole.java`를 엽니다.
2. `main` 메서드 왼쪽의 초록색 ▶ 버튼을 누르고 **Run**을 고릅니다.
3. 아래쪽 **Run 창을 클릭한 뒤** 입력하고 Enter를 누릅니다.

Run 창에서 입력이 되지 않으면 **Settings → Build, Execution, Deployment → Build Tools → Gradle**에서
**Build and run using**과 **Run tests using**을 **IntelliJ IDEA**로 바꾸고 다시 실행해 보세요.

빈 틀 그대로 실행하면 "아직 구현하지 않은 기능입니다 (N단계)"만 나옵니다. 도메인 코드와 컨트롤러를 채운 뒤 실행하세요.

## 새 문제 추가

1. 루트에 영문 폴더를 만들고 `README.md`를 둡니다. `src`가 있으면 `settings.gradle`이 자동으로 포함합니다.
2. 위 구조처럼 `api/XxxApi`(인터페이스), `api/XxxController`(모든 메서드가 `UnsupportedOperationException("N단계")`를 던지는 빈 틀), `console/XxxConsole`, `grading/XxxGradingTest`를 둡니다.
3. 채점 테스트가 검사하는 규칙은 README에 빠짐없이 적고, 예시 값은 테스트 값과 맞춥니다.
4. 위 문제 목록 표에 한 줄 추가합니다.
5. 정답은 `폴더명-answer`로 만들면 `.gitignore`에 걸려 올라가지 않습니다.
