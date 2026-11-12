# 가위바위보 구현하기

순수 자바(프레임워크 없음)로 가위바위보 경기를 구현합니다.
입출력(콘솔, 화면) 없이 **객체와 JUnit 테스트**만으로 요구사항을 검증합니다.

스터디의 **첫 문제**입니다. "규칙을 객체 안에 넣는다"는 감각을 가장 작은 문제로 연습합니다.

> 처음이라면 [루트 README](../README.md#진행-방법)에서 브랜치 만드는 법부터 보세요.

## 진행 규칙

- 패키지: `com.kdt.rps`
- 1단계부터 차례로 구현합니다. 따로 테스트를 작성할 필요는 없고, **채점 테스트로만 확인**합니다.
- 클래스 이름, 메서드 이름, 구조는 자유입니다. 설계 차이를 비교하는 것이 목적입니다.

## 1단계: 손 모양

- 손 모양은 **바위(`ROCK`), 보(`PAPER`), 가위(`SCISSORS`)** 세 가지입니다.
- 손 모양마다 **이기는 손**이 하나씩 있습니다.
- 이 세 이름 말고 다른 값은 `IllegalArgumentException`이 납니다. 대소문자와 공백까지 정확히 같아야 합니다.

| 손 | 이기는 손 |
|---|---|
| `ROCK` | `SCISSORS` |
| `SCISSORS` | `PAPER` |
| `PAPER` | `ROCK` |

| 입력 | 결과 | 이유 |
|---|---|---|
| `ROCK`, `PAPER`, `SCISSORS` | 허용 | |
| `rock`, `Rock` | 예외 | 대소문자가 다름 |
| ` ROCK`, `ROCK ` | 예외 | 앞뒤에 공백이 있음 |
| 빈 값, `가위`, `LIZARD` | 예외 | 없는 손 |

## 2단계: 판정

- 나와 상대의 손을 비교해 **승리(`WIN`), 패배(`LOSE`), 무승부(`DRAW`)** 를 판정합니다.
- 내 손이 상대 손을 이기면 승리, 상대 손이 내 손을 이기면 패배, 같은 손이면 무승부입니다.
- 나와 상대를 바꾸면 승패가 반대가 됩니다. 무승부는 그대로 무승부입니다.
- 둘 중 하나라도 잘못된 손이면 `IllegalArgumentException`이 납니다.

| 나 | 상대 | 결과 |
|---|---|---|
| `ROCK` | `SCISSORS` | `WIN` |
| `SCISSORS` | `PAPER` | `WIN` |
| `PAPER` | `ROCK` | `WIN` |
| `ROCK` | `PAPER` | `LOSE` |
| `SCISSORS` | `ROCK` | `LOSE` |
| `PAPER` | `SCISSORS` | `LOSE` |
| `ROCK` | `ROCK` | `DRAW` |
| `SCISSORS` | `SCISSORS` | `DRAW` |
| `PAPER` | `PAPER` | `DRAW` |

## 3단계: 전적

- 경기를 **시작**하면 전적이 **0승 0패 0무**입니다.
- 한 판을 할 때마다 2단계의 판정 결과를 돌려주고, 그 결과를 전적에 쌓습니다.
- 잘못된 손으로 하면 `IllegalArgumentException`이 나고 **전적은 그대로**입니다.
- **새로 시작하면** 전적이 0승 0패 0무로 돌아갑니다.

| 나 | 상대 | 결과 | 전적 |
|---|---|---|---|
| (시작) | | | 0승 0패 0무 |
| `ROCK` | `SCISSORS` | `WIN` | 1승 0패 0무 |
| `ROCK` | `PAPER` | `LOSE` | 1승 1패 0무 |
| `PAPER` | `PAPER` | `DRAW` | 1승 1패 1무 |
| `rock` | `SCISSORS` | `IllegalArgumentException` | 1승 1패 1무 |

## 4단계: 3선승 경기

- 경기 상태는 **진행 중(`PLAYING`), 승리(`WIN`), 패배(`LOSE`)** 세 가지입니다.
- 먼저 **3번 이기면 승리**, 먼저 **3번 지면 패배**하고 경기가 끝납니다.
- **비긴 판은 승부에 들어가지 않습니다.** 몇 번을 비겨도 경기는 계속됩니다. 무승부 수는 전적에 그대로 셉니다.
- 끝난 경기에서 한 판을 더 하면 `IllegalStateException`이 나고, 전적과 상태는 그대로입니다.
- 새로 시작하면 다시 진행 중이 됩니다.

| 진행 | 전적 | 상태 |
|---|---|---|
| (시작) | 0승 0패 0무 | `PLAYING` |
| 2번 이기고 2번 짐 | 2승 2패 0무 | `PLAYING` |
| 한 번 더 이김 | 3승 2패 0무 | `WIN` |
| 한 판 더 함 | 3승 2패 0무 | `IllegalStateException` |

| 진행 | 전적 | 상태 |
|---|---|---|
| 10번 비기고 2번 이김 | 2승 0패 10무 | `PLAYING` |

## 정해지지 않은 것 (자유)

아래는 채점하지 않으니 자유롭게 정하고, 왜 그렇게 정했는지 이야기해 보세요.

- 손 이름이 `null`이면?
- 경기를 시작하기 전에 한 판을 하거나 전적을 물으면?

## 채점하기

### 왜 컨트롤러가 필요한가요?

클래스 이름과 메서드 이름은 사람마다 다릅니다. 누구는 `Hand.beats()`, 누구는 `Referee.judge()`로 만들 수 있습니다.
채점 테스트가 특정 이름을 직접 부르면 모두가 그 이름에 맞춰야 하므로 설계가 똑같아집니다.

그래서 Spring의 컨트롤러 테스트처럼 **바깥 약속만 고정**합니다.
채점 테스트는 **정해진 인터페이스(`RockPaperScissorsApi`)만** 부르고, 각자 **`RockPaperScissorsController`에서 그 요청을 내 코드로 넘겨 줍니다.**
Spring 컨트롤러가 HTTP 요청을 받아 서비스로 넘기고, 결과를 응답 형식으로 바꿔 주는 것과 같은 역할입니다.

```
채점 테스트 ──▶ RockPaperScissorsApi ──▶ RockPaperScissorsController ──▶ 내 가위바위보 코드
 (고정)         (고정, 인터페이스)       (내가 채움)                     (설계 자유)
```

| Spring | 이 스터디 |
|---|---|
| HTTP 요청과 응답 형식 (고정) | `RockPaperScissorsApi` 인터페이스 (고정) |
| 컨트롤러: 요청을 받아 서비스로 넘김 | `RockPaperScissorsController`: 문자열과 숫자를 받아 내 객체로 넘김 |
| 서비스, 도메인 (설계 자유) | 내 가위바위보 코드 (설계 자유) |
| 컨트롤러 테스트 (MockMvc 등) | `RockPaperScissorsGradingTest` |

### 파일 구성

| 파일 | 역할 | 수정 |
|---|---|---|
| `src/main/java/com/kdt/rps/api/RockPaperScissorsApi.java` | 채점 테스트가 부르는 메서드 목록 | 하지 않음 |
| `src/main/java/com/kdt/rps/api/RockPaperScissorsController.java` | 요청을 내 코드로 넘기는 컨트롤러 | **여기만 채움** |
| `src/test/java/com/kdt/rps/grading/RockPaperScissorsGradingTest.java` | 채점 테스트 | 하지 않음 |

내 가위바위보 코드는 `com.kdt.rps` 아래에 자유롭게 만듭니다.

### 메서드별로 할 일

| 메서드 | 할 일 | 돌려줄 값 |
|---|---|---|
| `winsAgainst(hand)` | 이 손이 이기는 손 | `"ROCK"`, `"PAPER"`, `"SCISSORS"` |
| `judge(me, other)` | 한 번 판정하기 (전적에 쌓지 않음) | `"WIN"`, `"LOSE"`, `"DRAW"` |
| `start()` | 새 경기 시작 | 없음 |
| `play(me, other)` | 경기에서 한 판 하기 | `"WIN"`, `"LOSE"`, `"DRAW"` |
| `wins()`, `losses()`, `draws()` | 승, 패, 무 수 | 정수 |
| `status()` | 경기 상태 | `"PLAYING"`, `"WIN"`, `"LOSE"` |

### 채우는 예시

아래는 **이 문제와 상관없는 설계**를 예로 든 것입니다. 이름이 전혀 달라도 연결만 하면 된다는 것을 보여 주려는 예시이니, 이 구조를 따라 할 필요는 없습니다.

```java
public class RockPaperScissorsController implements RockPaperScissorsApi {
    private 경기 match;                                  // 1. 내 객체를 필드로 둔다

    @Override
    public void start() {
        match = new 경기();
    }

    @Override
    public String play(String me, String other) {
        결과 result = match.한판(손.이름으로(me), 손.이름으로(other));   // 2. 문자열을 내 객체로 바꿔 넘긴다
        return switch (result) {                          // 3. 내 값을 정해진 문자열로 바꿔 돌려준다
            case 이김 -> "WIN";
            case 짐 -> "LOSE";
            case 비김 -> "DRAW";
        };
    }

    // 나머지 메서드도 같은 방식
}
```

- 내 enum 상수 이름이 `ROCK`, `WIN`처럼 영문으로 같다면 `valueOf()`와 `name()`으로 바로 바꿀 수 있습니다.
- 채점 테스트는 테스트마다 `RockPaperScissorsController`를 새로 만들기 때문에 매번 처음 상태에서 시작합니다.
- **컨트롤러에는 로직을 넣지 않습니다.** 판정이나 검증을 여기서 하면 채점은 통과해도 가위바위보는 구현되지 않은 것입니다. 호출을 넘기고 값만 바꿔 주세요. Spring에서 컨트롤러에 비즈니스 로직을 넣지 않는 것과 같습니다.
- 아직 구현하지 않은 단계의 메서드는 `throw`를 그대로 두면 됩니다. 그 단계만 실패로 나옵니다.

### 실행

IntelliJ에서 `RockPaperScissorsGradingTest`를 열고 클래스 옆의 실행 버튼을 누르면 단계별로 결과가 보입니다.

```bash
./gradlew :rock-paper-scissors:test --tests '*GradingTest'
```

## 모여서 이야기할 거리

1. "바위는 가위를 이긴다"는 규칙을 어디에 두었나요? `if`문인가요, enum 안인가요, 표(Map)인가요?
2. 승패를 판정하는 것은 손 자신인가요, 따로 만든 심판인가요?
3. 판정 결과를 `String`으로 돌려주나요, enum으로 만들었나요?
4. 3선승을 5선승으로 바꾸려면 어디를 고쳐야 하나요?
5. 손 모양에 `LIZARD`가 새로 생긴다면 얼마나 많은 곳을 고쳐야 하나요?
