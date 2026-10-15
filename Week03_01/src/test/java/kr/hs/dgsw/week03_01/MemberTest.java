package kr.hs.dgsw.week03_01;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/** 1단계 : 순수 단위 테스트 - 그냥 자바객체를 new로 만들어 테스트
 * @Test : 이 메소드가 테스트입니다. 라는걸 명시, 없으면 실행안됨.
 * @DisplayName : 리포트에 보이는 이름 - 한글지원됨.
 * @Nested : 관련 테스트끼리 묶어서 계층으로 표시
 * */
@DisplayName("Member 엔티티를 테스트.")
class MemberTest {
    @Nested
    @DisplayName("Member 객체생성하면.")
    class Create{

        @Test
        @DisplayName("@AllArgsConstructor 로 id랑 name이 모두 채워짐")
        void allArgsConstructor() {

            //given, when -
            Member member = new Member(9L, "달봉이9");

            //then
            assertThat(member.getId()).isEqualTo(9L);
            assertThat(member.getName()).isEqualTo("달봉이9");

            assertAll(
                    () ->  assertThat(member.getId()).isEqualTo(9L),
                    () -> assertThat(member.getName()).isEqualTo("달봉이9")
            );
        }

        @Test
        @DisplayName("id는 아직 DB에 저장되기 전에는 null일수 있다.")
        void isCanBNullBeforePersist(){
            //JPA에서 id는 저장할때 DB가 채워줌 ( save() )
            //DB에 저장전에는 id가 null인게 정상.
            Member  member = new Member(null, "홍길동");

            assertThat(member.getId()).isNull();
            assertThat(member.getName()).isEqualTo("홍길동");
        }
    }


    @Nested
    @DisplayName("name 필드 테스트.")
    class NameField{

        @Test
        @DisplayName("@Setter가 붙어 있으면 수정할수 있다.")
        void nameIsMutable(){
            //given
            Member member = new Member(9L, "달봉이");
            //when
            member.setName("이순신");
            //then
            assertThat(member.getName()).isEqualTo("이순신");
        }

        @Test
        @DisplayName("@Setter가 없어서 수정안되요, id가요.")
        void idIsImmutable(){
            Member member = new Member(9L, "달봉이");

            /** PK id는 변경되면 안되기때문에 Setter을 막아둠.*/
            //member.setId(99L);
            assertThat(member.getId()).isEqualTo(9L);
        }

    }
}










