package kr.hs.dgsw.week07_pratice.service;

import kr.hs.dgsw.week07_pratice.domain.Member;

import java.util.List;

public interface MemberService {
    /**등록, 전체 조회, 단건 조회, 수정, 삭제 기능을 구현함. - CRUD*/
    Member create(Member member);
    List<Member> findAll();
    Member findById(Long id);
    Member update(Long id, Member m);
    boolean delete(Long id);

}
