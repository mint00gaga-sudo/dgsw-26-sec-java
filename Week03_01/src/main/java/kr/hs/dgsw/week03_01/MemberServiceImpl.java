package kr.hs.dgsw.week03_01;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/** 클라이언트가 요청한 작업을 수행.
 * 1. 데이터베이스에서 멤버정보 조회해서 Controller한테 전달. */
@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService {
    final MemberRepository memberRepository;

    @Override
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }
    @Override
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
    @Override
    public Member getMemberById(Long id) {
        return memberRepository.findById(id).orElse(null);
    }

    @Override
    public boolean deleteMemberById(Long id) {
        if( memberRepository.existsById(id) ) {
            memberRepository.deleteById(id);
            return true;
        }
        return false;
    }


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















