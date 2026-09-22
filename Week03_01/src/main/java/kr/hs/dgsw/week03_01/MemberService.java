package kr.hs.dgsw.week03_01;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;


public interface MemberService {
    List<Member> getAllMembers() ;
    Member saveMember(Member member) ;
    /** 데이터베이스에서 사용자가 전달한 id로 한놈 조회. */
    Member getMemberById(Long id) ;
    boolean deleteMemberById(Long id) ;
}


/**
 * 2026.09.22 - 코드 리팩토링
 * 1. Service 리팩토링
 * 2. 생성자 주입.
 * 3. Entity클래서 롬복.
 *
 * MemberService 클래스
 *  --> 인터페이스(모양, 틀, 구조) + 클래스( 실제 동작 )
 *
 * 컨트롤러에서 Service클래스를 호출
 *  --> Service인터페이스 호출.
 * */















