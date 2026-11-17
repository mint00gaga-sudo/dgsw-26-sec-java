package kr.hs.dgsw.week07_pratice.repository;

import kr.hs.dgsw.week07_pratice.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository
        extends JpaRepository<Member, Long> {
}
