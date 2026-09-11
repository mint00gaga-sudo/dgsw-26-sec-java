package kr.hs.dgsw.week03_01;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** 테이블을 조작하는 놈. Entity랑 무조건 1:1 .
 * JPA */

@Repository
public interface MemberRepository
        extends JpaRepository<Member, Long> {
}








