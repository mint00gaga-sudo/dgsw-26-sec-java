package kr.hs.dgsw.week08;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * ORM - Java로 디비를 컨트롤 할려고.
 * JPA ( Java Persistence API )
 *
 * 엔티티 - 디비랑 객체랑 1:1 맵핑되는 객체.
 * 엔티티 매니저 - 관리 ( 생성, 수정, 삭제, 조회, ... )
 * 엔티티 팩토리
 * Spring Data JPA 가 다함.
 *
 * 엔티티매니저가 엔티티를 저장하는 공간 --> 영속성 컨텍스트
 * 영속성 컨텍스트 란?
 *  - 엔티티가 저장되는 가상공간.
 *  - 엔티티 <-  영속성 컨텍스트 -> 데이터베이스
 *
 *  영속성 컨텍스트 특징.
 *  1. 1차캐시 - 속도증가 ( DB직접 거치지  않고 캐시에서 결과 전달 )
 *  2. 쓰기지연 - 쓸거를 모았다가 한번에 작성. (연결 횟수를 줄임)
 *   - 트랜젝션을 커밋하기 전까지 모았다가 한번에 실행
 *  3. 변경감지 - 1차캐시에 데이터와, 현재 엔티티의 데이터를 비교/변경 감지
 *                          --> 자동반영.
 *  4. Lazy Loading - 지연로딩.
 *
 *  관리를 쉽게하기 위해 --> 스프링 데이터 JPA, CRUD
 * */
@Repository
public interface MemberRepository
        extends JpaRepository<Member, Long> {
    Optional<Member> findByName(String name);
}











