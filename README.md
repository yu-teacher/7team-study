# 7팀 스터디

기능이나 주제를 **순수 자바**로 구현해 보고, 서로의 설계를 비교하는 스터디입니다.

## 문제 목록

| 문제 | 폴더 | 주요 연습 포인트 |
|---|---|---|
| 신호등 | [traffic-light](traffic-light) | enum, 상태 전이, 시간 흐름 처리 |

## 구조

```
7팀-스터디/
├── build.gradle        공통 설정 (Java 21, JUnit 5, AssertJ)
├── settings.gradle     src 폴더가 있는 하위 폴더를 자동으로 포함
├── traffic-light/
│   ├── README.md       요구사항
│   └── src/
└── ...
```

- 저장소 루트를 IntelliJ로 열면 모든 문제가 한 번에 보입니다.
- 문제 폴더에는 `build.gradle`이 없습니다. 루트 설정을 함께 씁니다.

## 진행 방법

1. `main` 브랜치에서 새 브랜치를 만듭니다. 이름은 `문제/이름` 형식입니다.
   ```bash
   git switch -c traffic-light/홍길동
   ```
2. 해당 문제 폴더의 README를 읽고 구현하며, 요구사항마다 테스트를 작성합니다.
3. 자기 브랜치에 푸시하고 `main`으로 PR을 엽니다. 서로 PR에 코드 리뷰를 남깁니다.
4. 스터디 때 README의 "모여서 이야기할 거리"로 설계를 비교합니다.

`main`은 문제(README와 빈 틀)만 두는 곳이라 PR은 병합하지 않습니다.

## 실행

```bash
./gradlew :traffic-light:test
```

## 새 문제 추가

1. 루트에 영문 폴더를 만들고 `README.md`와 `src/main/java`, `src/test/java`를 둡니다.
2. 위 문제 목록 표에 한 줄 추가합니다.
3. 정답은 `폴더명-answer`로 만들면 `.gitignore`에 걸려 올라가지 않습니다.
