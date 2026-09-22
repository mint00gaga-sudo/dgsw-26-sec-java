# Spring Boot 테스트 코드 작성 (Week03_01)

> **대상 프로젝트** : `Week03_01` — 회원(Member) CRUD REST API
> **환경** : Spring Boot 4.1.1 / Java 21 / JUnit 6 / Mockito 5.23 / H2
> **결과** : 총 **58개** 테스트, 전부 통과

---

## 목차

1. [왜 테스트 코드를 쓰는가](#1-왜-테스트-코드를-쓰는가)
2. [테스트의 기본 문법](#2-테스트의-기본-문법)
3. [테스트 대상 코드 (원본)](#3-테스트-대상-코드-원본)
4. [테스트 피라미드 — 6단계 지도](#4-테스트-피라미드--6단계-지도)
5. [0단계 : 스모크 테스트](#5-0단계--스모크-테스트)
6. [1단계 : 순수 단위 테스트](#6-1단계--순수-단위-테스트)
7. [2단계 : Mockito 서비스 테스트](#7-2단계--mockito-서비스-테스트)
8. [3단계 : @WebMvcTest 컨트롤러 테스트](#8-3단계--webmvctest-컨트롤러-테스트)
9. [4단계 : 요청 수신 방식 3종 테스트](#9-4단계--요청-수신-방식-3종-테스트)
10. [5단계 : @DataJpaTest 저장소 테스트](#10-5단계--datajpatest-저장소-테스트)
11. [6단계 : @SpringBootTest 통합 테스트](#11-6단계--springboottest-통합-테스트)
12. [테스트 전용 설정 파일](#12-테스트-전용-설정-파일)
13. [실행 방법](#13-실행-방법)
14. [Spring Boot 4 주의사항](#14-spring-boot-4-주의사항)
15. [치트시트](#15-치트시트)
16. [실습 과제](#16-실습-과제)

---

## 1. 왜 테스트 코드를 쓰는가

지금까지 여러분이 기능을 확인한 방법은 이랬을 것이다.

```
앱 실행 → Postman 켬 → URL 입력 → 전송 → 눈으로 확인 → 앱 종료
```

기능이 5개면 이 과정을 5번 반복한다. 기능이 50개가 되면? **아무도 전부 확인하지 않는다.**
그래서 "A를 고쳤더니 엉뚱한 B가 깨지는" 일이 생긴다.

테스트 코드는 이 확인 과정을 **코드로 박제**해 둔 것이다.

| | 수동 테스트 | 테스트 코드 |
|---|---|---|
| 50개 기능 확인 | 30분, 매번 | `./gradlew test` 15초 |
| 빠뜨림 | 자주 발생 | 없음 |
| 실패 원인 | 직접 추적 | 어느 줄인지 알려줌 |
| 사람이 바뀌면 | 인수인계 필요 | 코드가 곧 설명서 |

### 테스트의 또 다른 정체 : 문서

```java
@DisplayName("이름이 비어 있으면 null 을 반환하고 저장 자체를 하지 않는다")
```

이 한 줄이 **"우리 시스템의 규칙"** 을 말해 준다.
주석은 코드와 따로 놀며 낡아가지만, 테스트는 규칙이 깨지면 **빨갛게 실패해서 알려 준다**.
낡을 수 없는 유일한 문서다.

---

## 2. 테스트의 기본 문법

### given - when - then

모든 테스트는 이 3단 구조로 쓴다. 외워두면 평생 쓴다.

```java
@Test
void 이름_예시() {
    // given : 준비한다 — 테스트에 필요한 데이터와 상황을 만든다
    Member member = new Member(1L, "달봉이");

    // when : 실행한다 — 지금 검증하고 싶은 동작 "하나"만 호출한다
    member.setName("김유신");

    // then : 검증한다 — 결과가 기대와 같은지 확인한다
    assertThat(member.getName()).isEqualTo("김유신");
}
```

> **한 테스트는 한 가지만 검증한다.**
> 여러 개를 한 번에 확인하면, 실패했을 때 무엇이 문제인지 알 수 없다.

### 검증의 두 종류

```java
// ① 상태 검증 — "결과값이 맞는가?"
assertThat(result).isEqualTo("달봉이");

// ② 행위 검증 — "그 메소드를 호출했는가?"
verify(memberRepository).deleteById(1L);
verify(memberRepository, never()).save(any());   // 호출하지 "않았는지"도 검증
```

삭제 기능처럼 **리턴값이 `true`/`false` 뿐인 기능**은 상태 검증만으로는 부족하다.
"진짜로 지웠는가"는 행위 검증으로만 알 수 있다.

---

## 3. 테스트 대상 코드 (원본)

### 프로젝트 구조

```
src/main/java/kr/hs/dgsw/week03_01/
├── Week0301Application.java   애플리케이션 시작점
├── Member.java                엔티티     — DB 테이블과 1:1
├── MemberRepository.java      저장소     — 테이블 조작
├── MemberService.java         서비스     — 비즈니스 규칙(판단)
├── MemberController.java      컨트롤러   — URL 매핑
└── SearchController.java      컨트롤러   — 요청 수신 방식 예제
```

**요청이 흐르는 방향**

```
[브라우저]  →  Controller  →  Service  →  Repository  →  [DB]
             URL 매핑        판단/규칙     SQL 실행
```

### Member.java — 엔티티

```java
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Table(name = "member_tbl")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Setter
    @Column(name = "name", nullable = false)
    private String name;
}
```

- `id` 에는 `@Setter` 가 **없다** → 식별자는 바뀌면 안 되므로 일부러 막은 것
- `name` 에만 `@Setter` 가 있다 → 수정 가능한 값
- `@GeneratedValue(IDENTITY)` → id 는 DB 가 자동으로 채워 준다

### MemberService.java — 서비스 (핵심 검증 대상)

```java
@Service
public class MemberService {
    @Autowired
    MemberRepository memberRepository;

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    public Member saveMember(Member member) {
        String name = member.getName() == null ? null : member.getName().trim();

        if (StringUtils.hasText(name) == false) {
            return null;                    // ← 이름이 비면 저장 거부
        }
        member.setName(name);               // ← 공백 제거 후 저장
        return memberRepository.save(member);
    }

    public Member getMemberById(Long id) {
        return memberRepository.findById(id).orElse(null);   // ← 없으면 null
    }

    public boolean deleteMemberById(Long id) {
        if (memberRepository.existsById(id)) {
            memberRepository.deleteById(id);
            return true;
        }
        return false;                       // ← 없으면 삭제 시도조차 안 함
    }
}
```

**이 클래스가 담고 있는 "규칙" 4가지** — 전부 테스트로 못박을 대상이다.

1. 이름이 `null`/빈 문자열/공백뿐이면 저장하지 않는다
2. 이름 앞뒤 공백은 제거하고 저장한다
3. 없는 id 를 조회하면 예외가 아니라 `null` 을 준다
4. 없는 id 를 삭제하면 `false` 를 주고, `deleteById` 를 **호출하지 않는다**

### MemberController.java — REST API

```java
@RestController
public class MemberController {
    @Autowired
    MemberService memberService;

    @GetMapping("/member")
    public List<Member> getAllMembers() {
        return memberService.getAllMembers();
    }

    @PostMapping("/member")
    public Map<String, Object> addMember(@RequestBody Member member) {
        Map<String, Object> res = new HashMap<>();
        Member member1 = memberService.saveMember(member);

        if (member1 == null) {
            res.put("res", "fail");
            res.put("msg", "not value: [name]");
        } else {
            res.put("res", "success");
            res.put("msg", member1);
        }
        return res;
    }

    @GetMapping("/member/{id}")
    public Map<String, Object> getSingleMember(@PathVariable("id") Long userId) {
        Map<String, Object> res = new HashMap<>();
        Member foundMember = memberService.getMemberById(userId);

        if (foundMember == null) {
            res.put("res", "fail");
            res.put("msg", "not Member userId : " + userId);
        } else {
            res.put("res", "success");
            res.put("data", foundMember);
        }
        return res;
    }

    @DeleteMapping("/member/{id}")
    public Map<String, Object> deleteMember(@PathVariable("id") Long userId) {
        Map<String, Object> res = new HashMap<>();
        boolean isDelete = memberService.deleteMemberById(userId);

        if (isDelete == false) {
            res.put("res", "fail");
            res.put("msg", "not Member userId : " + userId);
        } else {
            res.put("res", "success");
            res.put("data", "delete complete");
        }
        return res;
    }
}
```

**API 명세**

| Method | URL | 성공 응답 | 실패 응답 |
|---|---|---|---|
| GET | `/member` | `[{...}, {...}]` | — |
| POST | `/member` | `{res:"success", msg:{...}}` | `{res:"fail", msg:"not value: [name]"}` |
| GET | `/member/{id}` | `{res:"success", data:{...}}` | `{res:"fail", msg:"not Member userId : 3"}` |
| DELETE | `/member/{id}` | `{res:"success", data:"delete complete"}` | `{res:"fail", msg:"..."}` |

### SearchController.java — 요청 수신 방식 3종

```java
@RestController
public class SearchController {

    /** @RequestParam : localhost:9090/search?keyword=노트북&page=3 */
    @GetMapping("/search")
    public String searchRequestParam(@RequestParam String keyword,
                                     @RequestParam int page) {
        return "키워드 : " + keyword + "는 [ " + page + "] 에 있음.";
    }

    /** @PathVariable : localhost:9090/user/5 */
    @GetMapping("/user/{id}")
    public String getUserInfo(@PathVariable("id") Long id) {
        return "사용자의 회원번호는 : " + id + " 입니다.";
    }

    /** @RequestBody : POST /danggun + JSON 본문 */
    @PostMapping("/danggun")
    public String notiDanggun(@RequestBody DangGun newDanggun) {
        return newDanggun.toString();
    }
}

@Getter @Setter @ToString
class DangGun {
    private String name;
    private String phone;
    private int price;
    private String si;
    private String gu;
}
```

---

## 4. 테스트 피라미드 — 6단계 지도

```
                  ▲  느리다 / 진짜에 가깝다
                 ╱ ╲
                ╱   ╲     6단계  @SpringBootTest     통합       4개
               ╱─────╲
              ╱       ╲   5단계  @DataJpaTest        저장소    10개
             ╱─────────╲  3~4단계 @WebMvcTest        웹        24개
            ╱           ╲
           ╱─────────────╲ 1~2단계 순수 + Mockito    단위      18개
          ╱_______________╲
              빠르다 / 많이 쓴다
```

> **아래를 두껍게, 위를 얇게.**
> 단위 테스트는 많이(빠르니까), 통합 테스트는 적게(느리니까) 쓴다.

| 단계 | 파일 | 무엇을 띄우나 | 속도 | 개수 |
|---|---|---|---|---|
| 0 | `Week0301ApplicationTests` | 전체 (기동 확인만) | 느림 | 2 |
| 1 | `MemberTest` | 아무것도 안 띄움 | ⚡ 즉시 | 4 |
| 2 | `MemberServiceTest` | 아무것도 안 띄움 (Mock) | ⚡ 즉시 | 14 |
| 3 | `MemberControllerTest` | 웹 계층만 | 보통 | 11 |
| 4 | `SearchControllerTest` | 웹 계층만 | 보통 | 13 |
| 5 | `MemberRepositoryTest` | JPA + H2 | 보통 | 10 |
| 6 | `MemberIntegrationTest` | 전체 | 느림 | 4 |

모든 테스트 파일 위치 :

```
src/test/java/kr/hs/dgsw/week03_01/
src/test/resources/application.yml
```

---

## 5. 0단계 : 스모크 테스트

### 개념

`contextLoads()` 는 아무것도 검증하지 않는 것처럼 보인다. 하지만 사실은
**"Spring 애플리케이션이 뜨기는 하는가?"** 를 검증한다.

설정 파일 오타, 빈 주입 실패, DB 연결 실패 — 이 중 하나라도 있으면
이 테스트 **하나**가 먼저 빨갛게 실패해서 알려 준다.
가장 싸게 얻는 안전장치다.

### 전체 코드 — `Week0301ApplicationTests.java`

```java
package kr.hs.dgsw.week03_01;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("애플리케이션 기동 확인")
class Week0301ApplicationTests {

    @Autowired MemberController memberController;
    @Autowired MemberService    memberService;
    @Autowired MemberRepository memberRepository;

    @Test
    @DisplayName("스프링 컨텍스트가 정상적으로 로딩된다")
    void contextLoads() {
    }

    @Test
    @DisplayName("Controller, Service, Repository 빈이 모두 등록되고 주입된다")
    void beansAreRegistered() {
        // @Autowired 주입이 실패하면 이 테스트까지 오지도 못한다.
        // 즉 아래 검증은 "3계층이 제대로 조립됐다"는 확인이다.
        assertThat(memberController).isNotNull();
        assertThat(memberService).isNotNull();
        assertThat(memberRepository).isNotNull();
    }
}
```

---

## 6. 1단계 : 순수 단위 테스트

### 개념

Spring 을 **전혀 띄우지 않는다**. 그냥 자바 객체를 `new` 로 만들어 확인한다.
실행 시간이 밀리초 단위다.

| 어노테이션 | 뜻 |
|---|---|
| `@Test` | 이 메소드가 테스트다. 없으면 실행되지 않는다 |
| `@DisplayName` | 리포트에 보일 이름. 한글 가능 |
| `@Nested` | 관련 테스트끼리 묶어 계층으로 표시 |

### 전체 코드 — `MemberTest.java`

```java
package kr.hs.dgsw.week03_01;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Member 엔티티")
class MemberTest {

    @Nested
    @DisplayName("객체를 생성하면")
    class Create {

        @Test
        @DisplayName("@AllArgsConstructor 로 id 와 name 이 모두 채워진다")
        void allArgsConstructor() {
            // given & when : 롬복이 만들어 준 (id, name) 생성자를 사용
            Member member = new Member(1L, "달봉이");

            // then : assertAll 로 묶으면 앞의 검증이 실패해도
            //        뒤의 검증까지 전부 실행되어 원인을 한 번에 볼 수 있다.
            assertAll(
                    () -> assertThat(member.getId()).isEqualTo(1L),
                    () -> assertThat(member.getName()).isEqualTo("달봉이")
            );
        }

        @Test
        @DisplayName("id 는 아직 DB 에 저장되기 전이면 null 일 수 있다")
        void idCanBeNullBeforePersist() {
            // JPA 에서 id 는 저장(save) 시점에 DB 가 채워준다.
            // 그래서 저장 전에는 null 인 상태가 정상이다.
            Member member = new Member(null, "홍길동");

            assertThat(member.getId()).isNull();
            assertThat(member.getName()).isEqualTo("홍길동");
        }
    }

    @Nested
    @DisplayName("name 필드는")
    class NameField {

        @Test
        @DisplayName("@Setter 가 붙어 있으므로 수정할 수 있다")
        void nameIsMutable() {
            // given
            Member member = new Member(1L, "달봉이");

            // when
            member.setName("김유신");

            // then
            assertThat(member.getName()).isEqualTo("김유신");
        }

        @Test
        @DisplayName("id 는 @Setter 가 없어서 외부에서 바꿀 수 없다(컴파일 자체가 불가)")
        void idIsImmutable() {
            Member member = new Member(1L, "달봉이");

            // member.setId(99L);  <-- 이 줄의 주석을 풀면 컴파일 에러가 난다.
            // 식별자(id)는 한번 정해지면 바뀌면 안 되는 값이므로
            // 일부러 Setter 를 막아둔 것이다. 이것도 "설계"다.
            assertThat(member.getId()).isEqualTo(1L);
        }
    }
}
```

### 💡 수업 포인트

`assertAll` 로 묶으면 **앞의 검증이 실패해도 뒤까지 전부 실행**된다.
그냥 나열하면 첫 실패에서 멈춰서, 두 번 고치고 두 번 돌려야 한다.

---

## 7. 2단계 : Mockito 서비스 테스트

### 개념 — 왜 가짜(Mock)를 쓰는가

`MemberService` 는 `MemberRepository` 에 의존한다.
그런데 지금 검증하고 싶은 것은 **"서비스의 판단 로직"** 이지 "DB 가 잘 도는지"가 아니다.

그래서 Repository 를 **가짜(Mock)** 로 바꿔 끼운다.

```
[ 진짜 ]  MemberService  →  MemberRepository  →  H2 DB    (느리다, 상황 연출이 어렵다)
[ Mock ]  MemberService  →  가짜Repository              (빠르다, 내 맘대로 연출)
```

- **장점 1** : DB 없이 돌아가므로 매우 빠르다
- **장점 2** : "DB 가 빈 리스트를 주면?", "없는 id 면?" 같은 상황을 **내 맘대로** 만들 수 있다

| 어노테이션 / 메소드 | 뜻 |
|---|---|
| `@ExtendWith(MockitoExtension.class)` | Mockito 를 JUnit 에 연결 |
| `@Mock` | 가짜 객체를 만든다 |
| `@InjectMocks` | 위에서 만든 가짜들을 이 객체에 주입해 준다 |
| `given(...).willReturn(...)` | "이렇게 호출되면 이걸 돌려줘" 시나리오 |
| `verify(...)` | "정말 그 메소드가 호출됐는지" 확인 |
| `ArgumentCaptor` | 넘어간 인자를 **낚아채서** 확인 |

### 전체 코드 — `MemberServiceTest.java`

```java
package kr.hs.dgsw.week03_01;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("MemberService 단위 테스트")
class MemberServiceTest {

    @Mock
    MemberRepository memberRepository;   // 가짜 저장소

    @InjectMocks
    MemberService memberService;         // 가짜를 주입받는 진짜 테스트 대상

    // ------------------------------------------------------------
    @Nested
    @DisplayName("getAllMembers() : 전체 조회")
    class GetAllMembers {

        @Test
        @DisplayName("저장소가 돌려준 목록을 그대로 반환한다")
        void returnsAll() {
            // given : 저장소가 2명을 돌려주는 상황을 연출
            List<Member> stub = List.of(new Member(1L, "달봉이"), new Member(2L, "홍길동"));
            given(memberRepository.findAll()).willReturn(stub);

            // when
            List<Member> result = memberService.getAllMembers();

            // then
            assertThat(result).hasSize(2)
                    .extracting(Member::getName)
                    .containsExactly("달봉이", "홍길동");

            // 행위 검증 : findAll 이 정확히 1번 호출되었는가
            verify(memberRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("데이터가 없으면 null 이 아니라 '빈 리스트'를 반환한다")
        void returnsEmptyList() {
            // 빈 결과를 null 로 주는지 빈 리스트로 주는지는 매우 중요하다.
            // null 이면 호출하는 쪽에서 NullPointerException 이 터지기 때문.
            given(memberRepository.findAll()).willReturn(List.of());

            List<Member> result = memberService.getAllMembers();

            assertThat(result).isNotNull().isEmpty();
        }
    }

    // ------------------------------------------------------------
    @Nested
    @DisplayName("saveMember() : 저장 + 이름 검증")
    class SaveMember {

        @Test
        @DisplayName("정상적인 이름이면 저장하고 저장된 회원을 반환한다")
        void saveSuccess() {
            // given
            Member request = new Member(null, "달봉이");
            Member saved   = new Member(1L, "달봉이");
            given(memberRepository.save(any(Member.class))).willReturn(saved);

            // when
            Member result = memberService.saveMember(request);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getName()).isEqualTo("달봉이");
        }

        @Test
        @DisplayName("이름 앞뒤 공백은 제거(trim)된 뒤 저장된다")
        void trimsName() {
            // given
            given(memberRepository.save(any(Member.class)))
                    .willReturn(new Member(1L, "달봉이"));

            // when : 일부러 공백을 잔뜩 붙여서 보낸다
            memberService.saveMember(new Member(null, "   달봉이   "));

            // then : ArgumentCaptor 는 "저장소에 실제로 넘어간 값"을 낚아채는 도구다.
            //        리턴값만 보면 trim 이 됐는지 알 수 없으므로 이렇게 확인한다.
            ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);
            verify(memberRepository).save(captor.capture());

            assertThat(captor.getValue().getName()).isEqualTo("달봉이");
        }

        @ParameterizedTest(name = "[{index}] 이름이 \"{0}\" 이면 저장하지 않는다")
        @NullSource                                       // null 을 한 번 넣어 본다
        @EmptySource                                      // "" 를 한 번 넣어 본다
        @ValueSource(strings = {" ", "   ", "\t", "\n"})   // 공백들
        @DisplayName("이름이 비어 있으면 null 을 반환하고 저장 자체를 하지 않는다")
        void rejectsBlankName(String invalidName) {
            // @ParameterizedTest : 같은 검증을 값만 바꿔 여러 번 돌린다.
            // 테스트 메소드를 6개 복사하지 않아도 되고, 경계값을 촘촘히 막을 수 있다.

            // when
            Member result = memberService.saveMember(new Member(null, invalidName));

            // then
            assertThat(result).isNull();

            // 가장 중요한 검증 : 잘못된 데이터가 DB 까지 내려가면 안 된다!
            verify(memberRepository, never()).save(any(Member.class));
        }
    }

    // ------------------------------------------------------------
    @Nested
    @DisplayName("getMemberById() : 1명 조회")
    class GetMemberById {

        @Test
        @DisplayName("존재하는 id 면 해당 회원을 반환한다")
        void found() {
            given(memberRepository.findById(1L))
                    .willReturn(Optional.of(new Member(1L, "이순신")));

            Member result = memberService.getMemberById(1L);

            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("이순신");
        }

        @Test
        @DisplayName("없는 id 면 예외가 아니라 null 을 반환한다")
        void notFound() {
            // 서비스 코드가 orElse(null) 이므로 예외가 아닌 null 이 나온다.
            // "없을 때 무엇을 돌려주는가"는 반드시 테스트로 못박아 둬야 한다.
            given(memberRepository.findById(999L)).willReturn(Optional.empty());

            Member result = memberService.getMemberById(999L);

            assertThat(result).isNull();
        }
    }

    // ------------------------------------------------------------
    @Nested
    @DisplayName("deleteMemberById() : 삭제")
    class DeleteMemberById {

        @Test
        @DisplayName("존재하는 id 면 삭제하고 true 를 반환한다")
        void deleteSuccess() {
            given(memberRepository.existsById(1L)).willReturn(true);

            boolean result = memberService.deleteMemberById(1L);

            assertThat(result).isTrue();
            verify(memberRepository).deleteById(1L);   // 진짜 삭제를 호출했는가?
        }

        @Test
        @DisplayName("없는 id 면 false 를 반환하고 삭제를 시도조차 하지 않는다")
        void deleteFail() {
            given(memberRepository.existsById(999L)).willReturn(false);

            boolean result = memberService.deleteMemberById(999L);

            assertThat(result).isFalse();
            // 없는 데이터를 지우려 들면 예외가 터지므로, 아예 호출하지 않아야 한다.
            verify(memberRepository, never()).deleteById(anyLong());
        }
    }
}
```

### 💡 수업 포인트 ①  `ArgumentCaptor` 가 필요한 이유

`trim()` 이 됐는지는 **리턴값만 봐서는 절대 알 수 없다.** 리턴값은 Mock 이 돌려준 가짜니까.
저장소로 **실제로 넘어간 객체**를 낚아채야 확인할 수 있다.

```java
ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);
verify(memberRepository).save(captor.capture());
assertThat(captor.getValue().getName()).isEqualTo("달봉이");   // ← 진짜 넘어간 값
```

### 💡 수업 포인트 ②  `never()` 가 진짜 검증이다

```java
verify(memberRepository, never()).save(any(Member.class));
```

"`null` 을 리턴했다" 보다 **"잘못된 데이터가 DB 까지 내려가지 않았다"** 가 훨씬 중요하다.
이건 `verify` 없이는 검증할 방법이 없다.

### 💡 수업 포인트 ③  `@ParameterizedTest`

`null`, `""`, `" "`, `"   "`, `"\t"`, `"\n"` — 테스트 메소드 **6개를 복사**하지 않고 한 개로 끝낸다.

```java
@NullSource                                      // null
@EmptySource                                     // ""
@ValueSource(strings = {" ", "   ", "\t", "\n"}) // 공백들
```

---

## 8. 3단계 : @WebMvcTest 컨트롤러 테스트

### 개념

`@WebMvcTest` 는 Spring 전체가 아니라 **웹 계층만** 띄운다.

| 띄우는 것 | 안 띄우는 것 |
|---|---|
| Controller, URL 매핑, JSON 변환기, 예외 처리 | Service 구현체, Repository, DB |

그래서 Service 는 `@MockitoBean` 으로 가짜를 등록해 줘야 한다.

> ⚠️ 예전 Spring Boot 의 `@MockBean` 은 **`@MockitoBean`** 으로 바뀌었다.

**MockMvc** 는 톰캣을 실제로 띄우지 않고 HTTP 요청을 흉내 내는 도구다.
포트를 열지 않으므로 빠르고, 네트워크 상태에 영향받지 않는다.

### 전체 코드 — `MemberControllerTest.java`

```java
package kr.hs.dgsw.week03_01;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
@DisplayName("MemberController 웹 계층 테스트")
class MemberControllerTest {

    @Autowired
    MockMvc mockMvc;              // 가짜 HTTP 요청을 쏘는 도구

    @MockitoBean
    MemberService memberService;  // 컨트롤러가 의존하는 서비스의 가짜

    // ------------------------------------------------------------
    @Nested
    @DisplayName("GET /member : 전체 조회")
    class GetAllMembers {

        @Test
        @DisplayName("회원 목록을 JSON 배열로 응답한다")
        void success() throws Exception {
            // given
            given(memberService.getAllMembers()).willReturn(
                    List.of(new Member(1L, "달봉이"), new Member(2L, "홍길동")));

            // when & then
            mockMvc.perform(get("/member"))
                    .andExpect(status().isOk())
                    // $ 는 응답 JSON 의 최상위를 뜻한다. 지금은 배열이다.
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].name").value("달봉이"))
                    .andExpect(jsonPath("$[1].name").value("홍길동"));
        }

        @Test
        @DisplayName("회원이 없으면 빈 배열을 응답한다 (200 OK 이다, 404 가 아니다)")
        void empty() throws Exception {
            given(memberService.getAllMembers()).willReturn(List.of());

            mockMvc.perform(get("/member"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }
    }

    // ------------------------------------------------------------
    @Nested
    @DisplayName("POST /member : 회원 추가")
    class AddMember {

        @Test
        @DisplayName("정상 저장되면 res=success 와 저장된 회원을 응답한다")
        void success() throws Exception {
            // given : 서비스가 저장에 성공한 상황
            given(memberService.saveMember(any(Member.class)))
                    .willReturn(new Member(1L, "달봉이"));

            // when & then
            mockMvc.perform(post("/member")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"name": "달봉이"}
                                    """))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.res").value("success"))
                    .andExpect(jsonPath("$.msg.id").value(1))
                    .andExpect(jsonPath("$.msg.name").value("달봉이"));
        }

        @Test
        @DisplayName("서비스가 null 을 반환하면(이름 부적합) res=fail 을 응답한다")
        void fail() throws Exception {
            // given : 이름이 비어 있어 서비스가 저장을 거부한 상황
            given(memberService.saveMember(any(Member.class))).willReturn(null);

            mockMvc.perform(post("/member")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"name": "   "}
                                    """))
                    // ※ 학습 포인트 :
                    //   실패인데도 상태코드는 200 OK 다. 본문 안의 res 값으로만 실패를 알린다.
                    //   원래는 400 Bad Request 를 주는 것이 REST 규약에 더 맞다.
                    //   테스트는 이렇게 "현재 설계가 무엇인지"를 드러내 준다.
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.res").value("fail"))
                    .andExpect(jsonPath("$.msg").value("not value: [name]"));
        }

        @Test
        @DisplayName("요청 JSON 의 name 이 Member 객체로 변환되어 서비스로 전달된다")
        void bodyIsBoundToObject() throws Exception {
            given(memberService.saveMember(any(Member.class)))
                    .willReturn(new Member(1L, "김유신"));

            mockMvc.perform(post("/member")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"name": "김유신"}
                                    """))
                    .andExpect(status().isOk());

            // @RequestBody 가 정말 동작해서 서비스까지 흘러갔는지 확인
            verify(memberService).saveMember(any(Member.class));
        }
    }

    // ------------------------------------------------------------
    @Nested
    @DisplayName("GET /member/{id} : 1명 조회")
    class GetSingleMember {

        @Test
        @DisplayName("존재하는 id 면 res=success 와 data 를 응답한다")
        void found() throws Exception {
            given(memberService.getMemberById(1L)).willReturn(new Member(1L, "이순신"));

            mockMvc.perform(get("/member/{id}", 1L))   // {id} 자리에 1 이 들어간다
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.res").value("success"))
                    .andExpect(jsonPath("$.data.name").value("이순신"));
        }

        @Test
        @DisplayName("없는 id 면 res=fail 과 안내 메시지를 응답한다")
        void notFound() throws Exception {
            given(memberService.getMemberById(999L)).willReturn(null);

            mockMvc.perform(get("/member/{id}", 999L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.res").value("fail"))
                    .andExpect(jsonPath("$.msg").value("not Member userId : 999"));
        }

        @Test
        @DisplayName("id 자리에 숫자가 아닌 값이 오면 400 Bad Request 가 된다")
        void invalidIdType() throws Exception {
            // @PathVariable("id") Long 이므로 "abc" 는 Long 으로 변환할 수 없다.
            // 컨트롤러 메소드가 호출되기도 전에 Spring 이 막아 준다.
            mockMvc.perform(get("/member/{id}", "abc"))
                    .andExpect(status().isBadRequest());
        }
    }

    // ------------------------------------------------------------
    @Nested
    @DisplayName("DELETE /member/{id} : 삭제")
    class DeleteMember {

        @Test
        @DisplayName("삭제에 성공하면 res=success 를 응답한다")
        void success() throws Exception {
            given(memberService.deleteMemberById(1L)).willReturn(true);

            mockMvc.perform(delete("/member/{id}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.res").value("success"))
                    .andExpect(jsonPath("$.data").value("delete complete"));

            verify(memberService).deleteMemberById(eq(1L));
        }

        @Test
        @DisplayName("없는 회원이면 res=fail 을 응답한다")
        void fail() throws Exception {
            given(memberService.deleteMemberById(999L)).willReturn(false);

            mockMvc.perform(delete("/member/{id}", 999L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.res").value("fail"))
                    .andExpect(jsonPath("$.msg").value("not Member userId : 999"));
        }

        @Test
        @DisplayName("같은 경로라도 HTTP 메소드가 다르면 다른 기능이다 (GET != DELETE)")
        void methodMatters() throws Exception {
            // /member/1 로 GET 을 보내면 조회가, DELETE 를 보내면 삭제가 실행된다.
            // URL 이 같아도 메소드로 기능을 구분하는 것이 REST 의 핵심이다.
            given(memberService.getMemberById(1L)).willReturn(new Member(1L, "달봉이"));

            mockMvc.perform(get("/member/{id}", 1L))
                    .andExpect(jsonPath("$.data.name").value("달봉이"));

            // 삭제 서비스는 호출되지 않았어야 한다.
            verify(memberService, org.mockito.Mockito.never()).deleteMemberById(1L);
        }
    }
}
```

### 💡 `jsonPath` 읽는 법

응답이 이렇다면 —

```json
{ "res": "success", "data": { "id": 1, "name": "이순신" } }
```

| 표현식 | 가리키는 값 |
|---|---|
| `$` | 최상위 전체 |
| `$.res` | `"success"` |
| `$.data.name` | `"이순신"` |
| `$[0].name` | 배열일 때 첫 번째의 name |
| `$.length()` | 배열 길이 |

### 💡 수업 포인트 — 테스트가 설계 결함을 드러낸다

저장 **실패**인데도 상태 코드가 `200 OK` 다.
REST 규약대로면 `400 Bad Request` 를 줘야 한다.
테스트를 쓰면 이런 게 눈에 보인다. **테스트는 설계 리뷰 도구이기도 하다.**

---

## 9. 4단계 : 요청 수신 방식 3종 테스트

### 개념 — 세 가지 방식 비교

| 어노테이션 | 데이터 위치 | 예시 URL / 본문 | 주 용도 |
|---|---|---|---|
| `@RequestParam` | URL 의 `?` 뒤 | `/search?keyword=노트북&page=3` | 검색·필터·페이징 |
| `@PathVariable` | URL 경로 자체 | `/user/5` | 특정 자원 지목 |
| `@RequestBody` | 요청 본문(JSON) | `POST /danggun` + `{...}` | 데이터 등록·수정 |

### 전체 코드 — `SearchControllerTest.java`

```java
package kr.hs.dgsw.week03_01;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SearchController.class)
@DisplayName("SearchController : 요청 데이터 수신 방식 테스트")
class SearchControllerTest {

    @Autowired
    MockMvc mockMvc;

    // ------------------------------------------------------------
    @Nested
    @DisplayName("@RequestParam : GET /search?keyword=&page=")
    class RequestParamTest {

        @Test
        @DisplayName("쿼리 파라미터 두 개를 받아 문자열로 조합해 응답한다")
        void success() throws Exception {
            mockMvc.perform(get("/search")
                            .param("keyword", "노트북")   // ?keyword=노트북
                            .param("page", "3"))          // &page=3
                    .andExpect(status().isOk())
                    .andExpect(content().string("키워드 : 노트북는 [ 3] 에 있음."));
        }

        @ParameterizedTest(name = "keyword={0}, page={1}")
        @CsvSource({
                "키보드, 1",
                "마우스, 10",
                "모니터, 999"
        })
        @DisplayName("여러 값 조합에서도 똑같이 동작한다")
        void variousValues(String keyword, int page) throws Exception {
            // @CsvSource 는 한 줄이 곧 한 번의 테스트다.
            // 표를 채우듯 케이스를 늘릴 수 있어 아주 편하다.
            mockMvc.perform(get("/search")
                            .param("keyword", keyword)
                            .param("page", String.valueOf(page)))
                    .andExpect(status().isOk())
                    .andExpect(content().string("키워드 : " + keyword + "는 [ " + page + "] 에 있음."));
        }

        @Test
        @DisplayName("필수 파라미터(page)가 빠지면 400 Bad Request 가 된다")
        void missingRequiredParam() throws Exception {
            // @RequestParam 은 기본값이 required = true 다.
            // 없으면 컨트롤러가 실행되지 않고 Spring 이 400 을 돌려준다.
            mockMvc.perform(get("/search").param("keyword", "노트북"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("page 에 숫자가 아닌 값을 주면 400 Bad Request 가 된다")
        void wrongParamType() throws Exception {
            // int page 로 선언했으므로 "abc" 는 변환 실패다.
            mockMvc.perform(get("/search")
                            .param("keyword", "노트북")
                            .param("page", "abc"))
                    .andExpect(status().isBadRequest());
        }
    }

    // ------------------------------------------------------------
    @Nested
    @DisplayName("@PathVariable : GET /user/{id}")
    class PathVariableTest {

        @Test
        @DisplayName("경로에 있는 숫자를 id 로 받아 응답한다")
        void success() throws Exception {
            mockMvc.perform(get("/user/{id}", 5L))
                    .andExpect(status().isOk())
                    .andExpect(content().string("사용자의 회원번호는 : 5 입니다."));
        }

        @Test
        @DisplayName("id 가 숫자가 아니면 400 Bad Request 가 된다")
        void wrongType() throws Exception {
            mockMvc.perform(get("/user/{id}", "hello"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("id 를 아예 빼면 매핑되는 URL 이 없어 404 가 된다")
        void missingPathVariable() throws Exception {
            // /user/{id} 는 /user 와 다른 주소다.
            // 400(요청이 잘못됨)이 아니라 404(그런 주소 없음)라는 점에 주의.
            mockMvc.perform(get("/user"))
                    .andExpect(status().isNotFound());
        }
    }

    // ------------------------------------------------------------
    @Nested
    @DisplayName("@RequestBody : POST /danggun")
    class RequestBodyTest {

        @Test
        @DisplayName("JSON 본문이 DangGun 객체로 변환되어 toString() 으로 응답된다")
        void success() throws Exception {
            mockMvc.perform(post("/danggun")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "name": "자전거",
                                      "phone": "010-1234-5678",
                                      "price": 50000,
                                      "si": "대구광역시",
                                      "gu": "달성군"
                                    }
                                    """))
                    .andExpect(status().isOk())
                    // @ToString 이 만든 형태 : DangGun(name=..., phone=..., ...)
                    .andExpect(content().string(
                            "DangGun(name=자전거, phone=010-1234-5678, price=50000, si=대구광역시, gu=달성군)"));
        }

        @Test
        @DisplayName("JSON 에 없는 필드는 기본값으로 채워진다 (String=null, int=0)")
        void partialBody() throws Exception {
            // 참조형(String)은 null, 기본형(int)은 0 이 된다.
            // 그래서 int price 는 "값을 안 보냈다"와 "0원이다"를 구분할 수 없다.
            // 이런 필드는 Integer 로 두는 편이 안전하다 — 테스트가 알려주는 설계 힌트다.
            mockMvc.perform(post("/danggun")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"name": "책상"}
                                    """))
                    .andExpect(status().isOk())
                    .andExpect(content().string(
                            "DangGun(name=책상, phone=null, price=0, si=null, gu=null)"));
        }

        @Test
        @DisplayName("Content-Type 이 JSON 이 아니면 415 Unsupported Media Type 이 된다")
        void wrongContentType() throws Exception {
            // @RequestBody 는 본문을 JSON 변환기로 읽는다.
            // text/plain 이라고 알려주면 읽을 변환기가 없어 415 가 난다.
            mockMvc.perform(post("/danggun")
                            .contentType(MediaType.TEXT_PLAIN)
                            .content("{\"name\":\"책상\"}"))
                    .andExpect(status().isUnsupportedMediaType());
        }

        @Test
        @DisplayName("본문이 깨진 JSON 이면 400 Bad Request 가 된다")
        void malformedJson() throws Exception {
            mockMvc.perform(post("/danggun")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{ 이건 JSON 이 아님 }"))
                    .andExpect(status().isBadRequest());
        }
    }
}
```

### 💡 수업 포인트 — 400 과 404 의 차이

| 상황 | 상태 코드 | 이유 |
|---|---|---|
| `/user/hello` (타입 불일치) | **400** Bad Request | 주소는 맞는데 **값**이 잘못됨 |
| `/user` (경로 자체 없음) | **404** Not Found | 그런 **주소**가 없음 |
| `/search?keyword=x` (page 누락) | **400** Bad Request | 필수 값이 빠짐 |
| Content-Type 이 text/plain | **415** Unsupported Media Type | 본문을 읽을 변환기가 없음 |

학생들이 가장 많이 헷갈리는 부분이므로 이 4개는 직접 돌려 보여 줄 것.

### 💡 `int price` 의 함정

```
{"name": "책상"}   →   DangGun(name=책상, phone=null, price=0, si=null, gu=null)
```

`price` 가 `0` 이다. 그런데 이건 **"안 보냈다"** 인가 **"0원이다"** 인가?
기본형(`int`)은 둘을 구분할 수 없다. `Integer` 로 두면 `null` 이 되어 구분 가능하다.
**테스트가 설계 힌트를 준 사례.**

---

## 10. 5단계 : @DataJpaTest 저장소 테스트

### 개념

지금까지는 Repository 를 **가짜**로 바꿔 썼다. 여기서는 반대로 **진짜 DB** 를 쓴다.
단, 메모리 위에서 도는 H2 를 쓴다.

**`@DataJpaTest` 의 특징**

1. JPA 관련 빈만 띄운다 (Controller, Service 는 안 띄움) → 가볍다
2. 각 테스트가 끝나면 **자동으로 롤백**된다 → 테스트끼리 서로 오염시키지 않는다
   그래서 어떤 순서로 실행되든 결과가 같다

**왜 이 테스트가 필요한가?**

Mock 은 "내가 정한 대로" 동작한다.
진짜 DB 의 제약조건(`nullable=false`), id 자동 생성, 실제 SQL 동작은
**Mock 으로 절대 검증할 수 없다.** 그 영역을 담당하는 것이 이 테스트다.

> ⚠️ Spring Boot 4 부터 `TestEntityManager` 가 **삭제**되었다.
> 표준 JPA 의 `jakarta.persistence.EntityManager` 를 직접 주입받아 쓴다.

### 전체 코드 — `MemberRepositoryTest.java`

```java
package kr.hs.dgsw.week03_01;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@DisplayName("MemberRepository : 실제 H2 DB 연동 테스트")
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager em;   // 영속성 컨텍스트를 직접 조작하기 위해 사용

    @BeforeEach
    void setUp() {
        // @BeforeEach : 각 @Test 가 실행되기 "직전"마다 한 번씩 실행된다.
        // 모든 테스트가 똑같이 깨끗한 상태에서 시작하도록 보장한다.
        memberRepository.deleteAll();
    }

    // ------------------------------------------------------------
    @Nested
    @DisplayName("save() : 저장")
    class Save {

        @Test
        @DisplayName("저장하면 id 가 자동으로 생성된다")
        void generatesId() {
            // given : id 를 null 로 두고 저장한다
            Member member = new Member(null, "달봉이");
            assertThat(member.getId()).isNull();

            // when
            Member saved = memberRepository.save(member);

            // then : @GeneratedValue(IDENTITY) 덕분에 DB 가 id 를 채워 준다
            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getName()).isEqualTo("달봉이");
        }

        @Test
        @DisplayName("id 가 다르면 이름이 같아도 각각 저장된다 (이름에 unique 제약이 없다)")
        void allowsDuplicateName() {
            memberRepository.save(new Member(null, "홍길동"));
            memberRepository.save(new Member(null, "홍길동"));

            assertThat(memberRepository.count()).isEqualTo(2);
        }

        @Test
        @DisplayName("name 이 null 이면 DB 제약조건(nullable=false)에 걸려 예외가 난다")
        void nameCannotBeNull() {
            Member member = new Member(null, null);

            // assertThatThrownBy : "이 코드는 반드시 예외가 나야 한다"를 검증한다.
            // flush() 를 호출해야 실제 INSERT SQL 이 DB 로 날아가면서 제약조건이 검사된다.
            assertThatThrownBy(() -> {
                memberRepository.save(member);
                em.flush();
            }).isInstanceOf(Exception.class);
        }
    }

    // ------------------------------------------------------------
    @Nested
    @DisplayName("findById() / findAll() : 조회")
    class Find {

        @Test
        @DisplayName("저장한 회원을 id 로 다시 찾을 수 있다")
        void findById() {
            // given
            Long id = memberRepository.save(new Member(null, "김유신")).getId();

            // when
            Optional<Member> found = memberRepository.findById(id);

            // then : findById 는 Optional 로 감싸서 준다 (null 대신)
            assertThat(found).isPresent();
            assertThat(found.get().getName()).isEqualTo("김유신");
        }

        @Test
        @DisplayName("없는 id 로 찾으면 Optional.empty() 가 반환된다")
        void findByIdNotFound() {
            Optional<Member> found = memberRepository.findById(99999L);

            // 예외가 나지 않고 "비어 있음"이 반환된다는 점이 핵심이다.
            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("findAll() 은 저장한 모든 회원을 돌려준다")
        void findAll() {
            memberRepository.save(new Member(null, "달봉이"));
            memberRepository.save(new Member(null, "홍길동"));
            memberRepository.save(new Member(null, "이순신"));

            List<Member> all = memberRepository.findAll();

            assertThat(all).hasSize(3)
                    .extracting(Member::getName)
                    .containsExactlyInAnyOrder("달봉이", "홍길동", "이순신");
        }

        @Test
        @DisplayName("데이터가 없으면 findAll() 은 빈 리스트를 돌려준다")
        void findAllEmpty() {
            assertThat(memberRepository.findAll()).isEmpty();
        }
    }

    // ------------------------------------------------------------
    @Nested
    @DisplayName("existsById() / deleteById() : 존재 확인과 삭제")
    class Delete {

        @Test
        @DisplayName("존재하는 id 면 existsById 가 true 다")
        void exists() {
            Long id = memberRepository.save(new Member(null, "이순신")).getId();

            assertThat(memberRepository.existsById(id)).isTrue();
            assertThat(memberRepository.existsById(id + 1000)).isFalse();
        }

        @Test
        @DisplayName("삭제하면 더 이상 조회되지 않는다")
        void deleteById() {
            // given
            Long id = memberRepository.save(new Member(null, "달봉이")).getId();
            assertThat(memberRepository.existsById(id)).isTrue();

            // when
            memberRepository.deleteById(id);
            em.flush();

            // then
            assertThat(memberRepository.existsById(id)).isFalse();
            assertThat(memberRepository.findById(id)).isEmpty();
        }
    }

    // ------------------------------------------------------------
    @Nested
    @DisplayName("더티 체킹(Dirty Checking)")
    class DirtyChecking {

        @Test
        @DisplayName("조회한 엔티티의 값을 바꾸면 save() 를 부르지 않아도 UPDATE 된다")
        void dirtyChecking() {
            // given
            Long id = memberRepository.save(new Member(null, "옛이름")).getId();
            em.flush();
            em.clear();   // 영속성 컨텍스트를 비워 진짜 DB 에서 다시 읽게 한다

            // when : 조회한 객체의 setter 만 호출한다. save() 는 부르지 않는다!
            Member found = memberRepository.findById(id).orElseThrow();
            found.setName("새이름");
            em.flush();
            em.clear();

            // then : JPA 가 변경을 감지해서 알아서 UPDATE 를 날려 준다
            assertThat(memberRepository.findById(id).orElseThrow().getName())
                    .isEqualTo("새이름");
        }
    }
}
```

### 💡 수업 포인트 — 더티 체킹

`save()` 를 **호출하지 않았는데도** UPDATE 가 나간다.

```java
Member found = memberRepository.findById(id).orElseThrow();
found.setName("새이름");     // ← setter 만 호출
em.flush();
// → UPDATE member_tbl SET name='새이름' WHERE id=?  SQL 이 자동으로 나간다
```

JPA 가 "조회 시점의 값"과 "지금 값"을 비교해서 **변경을 감지(Dirty Checking)** 하기 때문이다.
JPA 를 처음 배울 때 가장 신기해하는 부분이니 `show-sql: true` 로 SQL 을 직접 보여 줄 것.

---

## 11. 6단계 : @SpringBootTest 통합 테스트

### 개념

지금까지는 계층을 하나씩 떼어 내 가짜를 끼워 넣었다.
여기서는 **가짜가 하나도 없다.** 애플리케이션을 통째로 띄운다.

```
Controller  →  Service  →  Repository  →  실제 H2 DB
```

| | 단위 테스트 | 통합 테스트 |
|---|---|---|
| 속도 | 빠름 | 느림 |
| 실패 원인 | 콕 집어 줌 | 추적 어려움 |
| 검증 범위 | 로직 하나 | **조립까지 전부** |

> 둘 중 하나만 고르는 게 아니다. **단위를 많이 + 통합을 적당히** 가 정석이다.

`@Transactional` 을 붙인 이유 : 진짜 DB 에 데이터가 쌓이면 다음 테스트가 영향을 받는다.
테스트에 붙이면 **끝날 때마다 자동 롤백**되어 DB 가 원래대로 돌아간다.

### 전체 코드 — `MemberIntegrationTest.java`

```java
package kr.hs.dgsw.week03_01;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc   // @SpringBootTest 는 MockMvc 를 자동으로 안 만들어 주므로 직접 요청
@Transactional          // 각 테스트 종료 후 DB 롤백
@DisplayName("회원 기능 통합 테스트 (Controller~DB 전 구간)")
class MemberIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    MemberRepository memberRepository;   // 가짜가 아니라 진짜 저장소다

    @Test
    @DisplayName("등록 -> 전체조회 -> 단건조회 -> 삭제 -> 재조회 전체 시나리오")
    void fullScenario() throws Exception {
        // ---------- 1. 등록 ----------
        mockMvc.perform(post("/member")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "달봉이"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.res").value("success"));

        // DB 에 진짜로 들어갔는지 저장소로 직접 확인한다
        assertThat(memberRepository.count()).isEqualTo(1);
        Long id = memberRepository.findAll().get(0).getId();

        // ---------- 2. 전체 조회 ----------
        mockMvc.perform(get("/member"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("달봉이"));

        // ---------- 3. 단건 조회 ----------
        mockMvc.perform(get("/member/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.res").value("success"))
                .andExpect(jsonPath("$.data.name").value("달봉이"));

        // ---------- 4. 삭제 ----------
        mockMvc.perform(delete("/member/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.res").value("success"));

        assertThat(memberRepository.count()).isZero();

        // ---------- 5. 삭제된 회원 재조회 ----------
        mockMvc.perform(get("/member/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.res").value("fail"));
    }

    @Test
    @DisplayName("이름이 공백뿐이면 DB 에 한 건도 저장되지 않는다")
    void blankNameIsNotSaved() throws Exception {
        mockMvc.perform(post("/member")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "     "}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.res").value("fail"));

        // 응답만 믿지 말고 DB 를 직접 확인하는 습관을 들이자.
        assertThat(memberRepository.count()).isZero();
    }

    @Test
    @DisplayName("이름 앞뒤 공백은 제거되어 DB 에 저장된다")
    void nameIsTrimmedInDatabase() throws Exception {
        mockMvc.perform(post("/member")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "   홍길동   "}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg.name").value("홍길동"));

        assertThat(memberRepository.findAll().get(0).getName()).isEqualTo("홍길동");
    }

    @Test
    @DisplayName("없는 회원을 삭제하려 하면 res=fail 이고 DB 는 그대로다")
    void deleteNonExisting() throws Exception {
        memberRepository.save(new Member(null, "이순신"));

        mockMvc.perform(delete("/member/{id}", 99999L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.res").value("fail"));

        assertThat(memberRepository.count()).isEqualTo(1);
    }
}
```

### 💡 수업 포인트 — 응답만 믿지 마라

```java
.andExpect(jsonPath("$.res").value("success"));     // ① API 응답 확인
assertThat(memberRepository.count()).isEqualTo(1);  // ② DB 를 직접 확인 ★
```

API 가 `success` 라고 답해도 **DB 에 실제로 들어갔다는 보장은 없다.**
통합 테스트에서는 반드시 ②까지 확인하는 습관을 들일 것.

---

## 12. 테스트 전용 설정 파일

`src/test/resources/application.yml` 을 만들면
테스트 실행 시 **이 파일이 운영용 `application.yml` 대신** 사용된다.
(테스트 리소스가 클래스패스에서 더 앞에 놓이기 때문)

```yaml
# ============================================================
#  테스트 전용 설정 파일
# ------------------------------------------------------------
#  왜 따로 두는가?
#   1) 테스트는 언제 몇 번을 돌려도 같은 결과가 나와야 한다(재현 가능성).
#      data.sql 로 미리 4명을 넣어버리면 "회원 수는 1명" 같은 검증이
#      데이터에 휘둘려 깨진다. 그래서 초기 데이터 주입을 끈다.
#   2) 운영 설정(포트 9090 등)과 테스트 설정을 분리한다.
# ============================================================
spring:
  sql:
    init:
      mode: never           # data.sql / schema.sql 을 실행하지 않음
  jpa:
    hibernate:
      ddl-auto: create-drop # 테스트 시작 시 테이블 생성, 끝나면 삭제
    show-sql: true          # 실행되는 SQL 을 콘솔에서 확인(학습용)
    properties:
      hibernate:
        format_sql: true
```

---

## 13. 실행 방법

### 전체 테스트 실행

```bash
./gradlew test
```

### 특정 클래스만 실행

```bash
./gradlew test --tests "kr.hs.dgsw.week03_01.MemberServiceTest"
```

### 특정 메소드만 실행

```bash
./gradlew test --tests "*MemberServiceTest*trimsName"
```

### 결과 리포트 보기

```
build/reports/tests/test/index.html
```

브라우저로 열면 `@DisplayName` 에 적은 **한글 이름 그대로** 계층 구조로 보인다.

### IntelliJ 에서

- 클래스 왼쪽 **▶ 버튼** → 그 클래스 전체 실행
- 메소드 왼쪽 **▶ 버튼** → 그 메소드만 실행
- `Ctrl+Shift+F10` (Mac: `Ctrl+Shift+R`) → 커서 위치 테스트 실행

### 실행 결과

```
BUILD SUCCESSFUL
58 tests, 0 failed, 0 skipped
```

---

## 14. Spring Boot 4 주의사항

⚠️ **이 프로젝트는 Spring Boot 4.1.1 이다.**
인터넷의 예제와 교재 대부분은 Spring Boot 2.x / 3.x 기준이라 **그대로는 컴파일되지 않는다.**

| 항목 | 예전 (Boot 2~3) | **지금 (Boot 4.1)** |
|---|---|---|
| 서비스 가짜 주입 | `@MockBean` | **`@MockitoBean`**<br>`org.springframework.test.context.bean.override.mockito` |
| `@WebMvcTest` 패키지 | `org.springframework.boot.test.autoconfigure.web.servlet` | **`org.springframework.boot.webmvc.test.autoconfigure`** |
| `@AutoConfigureMockMvc` | 〃 | **`org.springframework.boot.webmvc.test.autoconfigure`** |
| `@DataJpaTest` 패키지 | `org.springframework.boot.test.autoconfigure.orm.jpa` | **`org.springframework.boot.data.jpa.test.autoconfigure`** |
| `TestEntityManager` | 있음 | **삭제됨** → `jakarta.persistence.EntityManager` 사용 |
| JUnit | 5.x | **6.0** |
| Jackson | `com.fasterxml.jackson` | **`tools.jackson`** |
| 웹 스타터 | `spring-boot-starter-web` | **`spring-boot-starter-webmvc`** |
| 테스트 스타터 | `spring-boot-starter-test` | **`-webmvc-test`, `-data-jpa-test`** 로 분리 |

### build.gradle 의존성

```gradle
dependencies {
    implementation 'org.springframework.boot:spring-boot-h2console'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-webmvc'
    compileOnly 'org.projectlombok:lombok'
    runtimeOnly 'com.h2database:h2'
    annotationProcessor 'org.projectlombok:lombok'

    testImplementation 'org.springframework.boot:spring-boot-starter-data-jpa-test'
    testImplementation 'org.springframework.boot:spring-boot-starter-webmvc-test'
    testCompileOnly 'org.projectlombok:lombok'
    testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
    testAnnotationProcessor 'org.projectlombok:lombok'
}

tasks.named('test') {
    useJUnitPlatform()
}
```

---

## 15. 치트시트

### 어노테이션 한눈에

| 어노테이션 | 범위 | 언제 쓰나 |
|---|---|---|
| `@Test` | 메소드 | 이게 테스트다 |
| `@DisplayName` | 클래스/메소드 | 리포트에 보일 한글 이름 |
| `@Nested` | 내부 클래스 | 관련 테스트 묶기 |
| `@BeforeEach` / `@AfterEach` | 메소드 | 각 테스트 전/후 실행 |
| `@ParameterizedTest` | 메소드 | 값만 바꿔 여러 번 실행 |
| `@ValueSource` / `@CsvSource` / `@NullSource` / `@EmptySource` | 메소드 | 위에 넣을 값들 |
| `@ExtendWith(MockitoExtension.class)` | 클래스 | Mockito 연결 |
| `@Mock` / `@InjectMocks` | 필드 | 가짜 만들기 / 주입하기 |
| `@WebMvcTest(X.class)` | 클래스 | 웹 계층만 띄우기 |
| `@MockitoBean` | 필드 | 스프링 컨텍스트에 가짜 빈 등록 |
| `@DataJpaTest` | 클래스 | JPA + H2 만 띄우기 |
| `@SpringBootTest` | 클래스 | 전체 띄우기 |
| `@AutoConfigureMockMvc` | 클래스 | `@SpringBootTest` 에 MockMvc 추가 |
| `@Transactional` | 클래스/메소드 | 테스트 후 자동 롤백 |

### AssertJ — `assertThat(...)`

```java
assertThat(x).isEqualTo("값");          // 같은가
assertThat(x).isNull();                 // null 인가
assertThat(x).isNotNull();              // null 이 아닌가
assertThat(x).isTrue();  .isFalse();    // 참/거짓
assertThat(list).isEmpty();             // 비었는가
assertThat(list).hasSize(3);            // 크기
assertThat(list).contains("a", "b");    // 포함하는가
assertThat(list).extracting(Member::getName)
                .containsExactly("달봉이", "홍길동");           // 순서까지 일치
assertThat(list).extracting(Member::getName)
                .containsExactlyInAnyOrder("홍길동", "달봉이"); // 순서 무관
assertThat(opt).isPresent();  .isEmpty();   // Optional
assertThatThrownBy(() -> ...).isInstanceOf(Exception.class);  // 예외 검증
```

### Mockito

```java
given(repo.findAll()).willReturn(List.of(...));   // 이렇게 호출되면 이걸 돌려줘
given(repo.findById(1L)).willReturn(Optional.of(m));
given(repo.existsById(1L)).willReturn(true);

verify(repo).save(any());                 // 호출됐는가
verify(repo, times(2)).findAll();         // 2번 호출됐는가
verify(repo, never()).deleteById(any());  // 호출 안 됐는가 ★

ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);
verify(repo).save(captor.capture());
captor.getValue();                        // 실제로 넘어간 인자
```

### MockMvc

```java
mockMvc.perform(get("/member"))                          // GET
mockMvc.perform(get("/member/{id}", 1L))                 // PathVariable
mockMvc.perform(get("/search").param("keyword", "x"))    // RequestParam
mockMvc.perform(post("/member")                          // POST + JSON
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
                {"name": "달봉이"}
                """))
mockMvc.perform(delete("/member/{id}", 1L))              // DELETE

    .andExpect(status().isOk())                    // 200
    .andExpect(status().isBadRequest())            // 400
    .andExpect(status().isNotFound())              // 404
    .andExpect(status().isUnsupportedMediaType())  // 415
    .andExpect(jsonPath("$.res").value("success"))
    .andExpect(jsonPath("$.data.name").value("이순신"))
    .andExpect(jsonPath("$.length()").value(2))
    .andExpect(content().string("문자열 전체 일치"))
    .andDo(print());                               // 요청/응답 전체를 콘솔에 출력(디버깅용)
```

### 자주 만나는 오류

| 증상 | 원인 | 해결 |
|---|---|---|
| `NullPointerException` (서비스 테스트) | `@InjectMocks` 를 안 붙임 | 테스트 대상에 `@InjectMocks` |
| `UnnecessaryStubbingException` | `given` 을 해놓고 안 씀 | 안 쓰는 `given` 삭제 |
| `NoSuchBeanDefinitionException` (`@WebMvcTest`) | Service 가짜를 안 만듦 | `@MockitoBean` 추가 |
| 테스트 순서 따라 결과가 달라짐 | DB 가 오염됨 | `@Transactional` 또는 `@BeforeEach` 초기화 |
| `cannot find symbol: MockBean` | Boot 4 에서 이름 바뀜 | `@MockitoBean` 사용 |
| `@WebMvcTest` import 안 됨 | Boot 4 에서 패키지 이동 | `org.springframework.boot.webmvc.test.autoconfigure` |

---

## 16. 실습 과제

### 🟢 초급 — 기존 테스트 따라 만들기

1. `MemberTest` 에 "이름을 빈 문자열로 바꿀 수 있는가" 테스트를 추가하라.
   (엔티티 레벨에서는 막혀 있지 않다는 것을 확인 — **이것이 서비스 검증이 필요한 이유**)
2. `MemberServiceTest` 에 회원이 100명일 때 `getAllMembers()` 가 100개를 반환하는지 추가하라.
3. `SearchControllerTest` 의 `@CsvSource` 에 케이스 3개를 더 추가하라.

### 🟡 중급 — 새 기능 + 테스트

4. `MemberService` 에 `updateMemberName(Long id, String newName)` 을 추가하고,
   다음 3가지를 모두 테스트하라.
   - 정상 수정
   - 없는 id → `null` 반환
   - 새 이름이 공백 → 수정하지 않음 (`verify ... never()`)
5. `MemberController` 에 `PUT /member/{id}` 를 추가하고 `@WebMvcTest` 로 검증하라.

### 🔴 고급 — 설계 개선

6. `MemberRepository` 에 `findByName(String name)` 을 추가하고 `@DataJpaTest` 로 검증하라.
   (동명이인이 있으면? → 반환 타입을 `List<Member>` 로 해야 하는 이유를 테스트로 증명)
7. 저장 실패 시 `200 OK` 대신 **`400 Bad Request`** 를 주도록 컨트롤러를 고치고,
   테스트를 먼저 고친 뒤(빨강) → 코드를 고쳐 통과시켜라(초록).
   **이것이 TDD 다.**
8. `DangGun` 의 `int price` 를 `Integer` 로 바꾸고,
   "값을 안 보냈을 때 `null` 이 된다"를 테스트로 증명하라.

---

## 부록 A. 전체 소스 코드 (main)

> 테스트 대상이 되는 프로젝트 원본 전체다. 이 파일 하나만 있으면 프로젝트를 재현할 수 있다.

### `build.gradle`

```gradle
plugins {
    id 'java'
    id 'org.springframework.boot' version '4.1.1'
    id 'io.spring.dependency-management' version '1.1.7'
}

group = 'kr.hs.dgsw'
version = '-'
description = 'Week03_01'

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-h2console'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-webmvc'
    compileOnly 'org.projectlombok:lombok'
    runtimeOnly 'com.h2database:h2'
    annotationProcessor 'org.projectlombok:lombok'
    testImplementation 'org.springframework.boot:spring-boot-starter-data-jpa-test'
    testImplementation 'org.springframework.boot:spring-boot-starter-webmvc-test'
    testCompileOnly 'org.projectlombok:lombok'
    testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
    testAnnotationProcessor 'org.projectlombok:lombok'
}

tasks.named('test') {
    useJUnitPlatform()
}
```

### `settings.gradle`

```gradle
rootProject.name = 'Week03_01'
```

### `src/main/resources/application.yml`

```yaml
spring:
  jpa:
    show-sql: true
    properties:
      hibernate:
        format_sql: true
    defer-datasource-initialization: true
server:
  port: 9090
```

### `src/main/resources/data.sql`

```sql
INSERT INTO member( name) VALUES('달봉이')
INSERT INTO member( name) VALUES('홍길동')
INSERT INTO member( name) VALUES('김유신')
INSERT INTO member( name) VALUES('이순신')
```

> ⚠️ **알려진 버그** — 각 줄 끝에 세미콜론(`;`)이 없다.
> 세미콜론이 없으면 4줄이 하나의 SQL 로 합쳐져 문법 오류가 나고, **앱이 기동되지 않는다.**
> 또한 테이블명이 `member` 인데 엔티티는 `@Table(name="member_tbl")` 이라 이름도 맞지 않는다.
>
> 올바른 형태:
> ```sql
> INSERT INTO member_tbl(name) VALUES('달봉이');
> INSERT INTO member_tbl(name) VALUES('홍길동');
> INSERT INTO member_tbl(name) VALUES('김유신');
> INSERT INTO member_tbl(name) VALUES('이순신');
> ```

### `Week0301Application.java`

```java
package kr.hs.dgsw.week03_01;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Week0301Application {

    public static void main(String[] args) {
        SpringApplication.run(Week0301Application.class, args);
    }

}
```

### `Member.java`

```java
package kr.hs.dgsw.week03_01;

import jakarta.persistence.*;
import lombok.*;

/** 엔티티(Entity) 클래스,
 * 데이터베이스 테이블과 1:1 매칭. */

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Table(name="member_tbl")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column( name="id", nullable = false)
    private Long id;

    @Setter
    @Column(name="name", nullable = false)
    private String name;

}
```

### `MemberRepository.java`

```java
package kr.hs.dgsw.week03_01;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** 테이블을 조작하는 놈. Entity랑 무조건 1:1 .
 * JPA */

@Repository
public interface MemberRepository
        extends JpaRepository<Member, Long> {
}
```

### `MemberService.java`

```java
package kr.hs.dgsw.week03_01;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/** 클라이언트가 요청한 작업을 수행.
 * 1. 데이터베이스에서 멤버정보 조회해서 Controller한테 전달. */
@Service
public class MemberService {
    @Autowired
    MemberRepository memberRepository;

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    public Member saveMember(Member member) {
        String name
          = member.getName() == null ? null : member.getName().trim();

        if( StringUtils.hasText(name) == false) {
            return null;
        }
        member.setName(name);
        return memberRepository.save(member);
    }

    /** 데이터베이스에서 사용자가 전달한 id로 한놈 조회. */
    public Member getMemberById(Long id) {
        return memberRepository.findById(id).orElse(null);
    }

    public boolean deleteMemberById(Long id) {
        if( memberRepository.existsById(id) ) {
            memberRepository.deleteById(id);
            return true;
        }
        return false;
    }


}
```

### `MemberController.java`

```java
package kr.hs.dgsw.week03_01;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** 클라이언트 url에 맵핑되는 메소드를 호출.
 * @RequestParam
 * @PathVariable
 * @RequestBody
 * @ModelAttribute
 * @RequestPart
 * @ReqeustHeader
 *
 *
 * */
@RestController
public class MemberController {
    @Autowired
    MemberService memberService;

    @GetMapping("/member")
    public List<Member> getAllMembers() {
        List<Member> members = memberService.getAllMembers();
        return members;
    }

    /** 멤버추가. */
    @PostMapping("/member")
    public Map<String, Object>
            addMember(@RequestBody Member member) {
        /** 회원정보 입력 --> 서버에 전송 --> 디비에 저장 --> 응답.*/
        Map<String, Object> res = new HashMap<>();
        Member member1 = memberService.saveMember(member);

        if( member1 == null){
            /** 데이터가 비정상*/
            res.put("res", "fail");
            res.put("msg", "not value: [name]");
        }else{
            /** 정상 데이터. */
            res.put("res", "success");
            res.put("msg", member1);
        }
        return res;
    }

    /** 회원 1명 조회. */
    @GetMapping("/member/{id}")
    public Map<String, Object> getSingleMember(@PathVariable("id") Long userId) {
        Map<String, Object> res = new HashMap<>();
        Member foundMember = memberService.getMemberById( userId );

        if(foundMember == null){
            res.put("res", "fail");
            res.put("msg", "not Member userId : "+userId);
        }else{
            res.put("res", "success");
            res.put("data", foundMember);
        }

        return res;

    }


    /** 단일 삭제, Http Method 는 Delete로 작성.
     * Controller, Servive 메소드. */
    /** 회원 1명 삭제. */
    @DeleteMapping("/member/{id}")
    public Map<String, Object> deleteMember(@PathVariable("id") Long userId) {
        Map<String, Object> res = new HashMap<>();
        boolean isDelete = memberService.deleteMemberById( userId );

        if(isDelete == false){
            res.put("res", "fail");
            res.put("msg", "not Member userId : "+userId);
        }else{
            res.put("res", "success");
            res.put("data", "delete complete");
        }
        return res;
    }
}
```

### `SearchController.java`

```java
package kr.hs.dgsw.week03_01;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


/**
 * http 요청데이터 수신 종류
 * @RequestParam  **
 * @RequestBody   **
 * @PathVariable  **
 *
 * @RequestPart
 * @RequestHeader
 * @ModelAttribute
 * @CookieValue
 *
 *
 *
 * */
@RestController
public class SearchController {

    /** @RequestParam
     * localhost:9090/search?keyword=노트북&page=3   */
    @GetMapping("/search")
    public String searchRequestParam(
            @RequestParam String keyword,
            @RequestParam int page
    ){
        return "키워드 : "+keyword + "는 [ "+page+"] 에 있음.";
    }

    /** @PathVariable 경로에 변수없이 데이터를 바로 입력.
     * 테스트 : localhost:9090/user/5      */
    @GetMapping("/user/{id}")
    public String getUserInfo(@PathVariable("id") Long id){
        return "사용자의 회원번호는 : "+id+" 입니다.";
    }

    /** @RequestBody */
    @PostMapping("/danggun")
    public String notiDanggun(@RequestBody DangGun newDanggun){
        return newDanggun.toString();
    }
}

@Getter
@Setter
@ToString
class DangGun{
    private String name;
    private String phone;
    private int price;
    private String si;
    private String gu;

}
```

---

## 부록 B. 파일 배치도

이 문서의 테스트 코드를 프로젝트에 넣을 때의 위치다.

```
Week03_01/
├── build.gradle
├── settings.gradle
├── TESTING_GUIDE.md                    ← 이 문서
└── src/
    ├── main/
    │   ├── java/kr/hs/dgsw/week03_01/
    │   │   ├── Week0301Application.java
    │   │   ├── Member.java
    │   │   ├── MemberRepository.java
    │   │   ├── MemberService.java
    │   │   ├── MemberController.java
    │   │   └── SearchController.java
    │   └── resources/
    │       ├── application.yml
    │       └── data.sql
    └── test/
        ├── java/kr/hs/dgsw/week03_01/
        │   ├── Week0301ApplicationTests.java   0단계  스모크        2개
        │   ├── MemberTest.java                 1단계  순수 단위      4개
        │   ├── MemberServiceTest.java          2단계  Mockito      14개
        │   ├── MemberControllerTest.java       3단계  @WebMvcTest  11개
        │   ├── SearchControllerTest.java       4단계  @WebMvcTest  13개
        │   ├── MemberRepositoryTest.java       5단계  @DataJpaTest 10개
        │   └── MemberIntegrationTest.java      6단계  통합           4개
        └── resources/
            └── application.yml                 테스트 전용 설정
```

**합계 : 58개 테스트**

---

## 한 줄 정리

> **테스트 코드는 "내가 만든 규칙"을 코드로 박제해서,
> 누가 언제 코드를 고쳐도 그 규칙이 깨지면 즉시 알려주는 장치다.**

```
단위 테스트를 많이 (빠르니까)  +  통합 테스트를 적당히 (진짜니까)
```
