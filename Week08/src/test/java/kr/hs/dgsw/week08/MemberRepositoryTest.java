package kr.hs.dgsw.week08;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/** DataJpsTest : 스프링부트에서 테스트 전용 어노테이션
 * JPA 컴포넌트만 로드해서 데이터를 테스트. */
@DataJpaTest
class MemberRepositoryTest {
    @Autowired
    private MemberRepository mRep;

    /** 조회 */
    @Test
    @Sql("/insert-members.sql")
    void getAllMembers(){
        //when
        List<Member> all = mRep.findAll();

        //then
        assertThat(all.size()).isEqualTo(3);
    }

    @Test
    @Sql("/insert-members.sql")
    void getfindById(){
        Optional<Member> byId = mRep.findById(2L);
        assertThat(byId.orElse(null).getName()).isEqualTo("22번");

        /** select * from member WHERE name = '달봉이' */
    }

    /** 쿼리메소드 - 메소드 이름으로 쿼리를 실행해주는 기능 - JPA라는 놈이 */
    @Test
    @Sql("/insert-members.sql")
    void getfindByName(){
        Optional<Member> byName = mRep.findByName("33333");
        System.out.println(byName.get());
    }

    /** 쿼리메소드
     * And, Or, 조건연결.
     * 크가, 작다, 크거나 작다, LessThan, GreaterThan
     * 정렬 : OrderBy
     *
     * */


    /** 수정 */
    @Test
    @Sql("/insert-members.sql")
    void updateMember(){
        //insert into member VALUES (1L, '1번이름')
        Member mem1 = new Member("111");
        Member mem2 = new Member(1L, "222");
        //mRep.save(mem1);
        assertThat(mRep.findById(1L).get().getName()).isEqualTo("1번이름");
        mRep.save(mem2);
        assertThat(mRep.findById(1L).get().getName()).isEqualTo("1번이름");
    }

    @Test
    @Sql("/insert-members.sql")
    void updateMember_v2(){
        //insert into member VALUES (1L, '1번이름')
        Member dbMember = mRep.findById(1L).get();
        dbMember.updateMemberName("달봉이");

        assertThat(mRep.findById(1L).get().getName())
                .isEqualTo("달봉이");
    }


}








