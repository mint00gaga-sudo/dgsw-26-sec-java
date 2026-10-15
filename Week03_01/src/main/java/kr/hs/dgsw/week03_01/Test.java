package kr.hs.dgsw.week03_01;
/**
 * 프로젝트 실행 --> 포스트맨 실행 --> 컨트롤러 기준 url
 * 테스트 1회 : 몇십분. --> 테스트 코드 --> 10초.
 *
 * @DisplayName("이름이 비어있으면 null을 반환, 저장안함.")
 *  --> 시스템규칙. 맞으면 녹색, 틀리면 노란/빨간색.
 *
 *  테스트 기본 문법. given -> when --> then
 * */
public class Test {
    /** 테스트는 한번에 한개만 검증. */
    void test01(){
        //given - 준비 - 테스트를 위해서 필요한 데이터, 상황 준비.
        Member member = new Member(1L, "달봉이");

        //when - 검증조건 - 검증하고 싶은 동작(메소드)를 호출
        member.setName("홍길동");

        //then : 검증 - 결과가 기대한거랑 같냐?
        //assertThat(member.getName()).isEqualsTo("홍길동");
    }
    /** 검증 종류
     * 상태에 대한 검증 - 결과값이 맞냐?
     *  - assertThat(member.getName()).isEqualsTo("홍길동")
     *
     * 동작에 대한 검증. - 원하는 메소드를 호출했냐?
     * memberRepository.deleteById(1L);
     *  --> verify(memberRepository).deleteById(1L)
     *
     * 브라우저 --> Controller ( URL 맵핑 ) --> Service ( 판단, 규칙 )
     *  --> Repository ( SQL 실행 ) --> DB ( Mysql, )
     *
     * */
}







