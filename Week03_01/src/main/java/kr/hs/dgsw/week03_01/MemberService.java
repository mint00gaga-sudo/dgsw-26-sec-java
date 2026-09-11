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


}














