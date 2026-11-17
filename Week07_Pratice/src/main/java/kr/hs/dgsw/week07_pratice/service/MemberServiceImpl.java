package kr.hs.dgsw.week07_pratice.service;

import kr.hs.dgsw.week07_pratice.domain.Member;
import kr.hs.dgsw.week07_pratice.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/** 변구 Repository --> 생성자 주입.
 * @Service 는 클래스에 붙인다. */
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{

    private final MemberRepository memberRepository;

    @Override
    public Member create(Member member) {
        if(StringUtils.hasText(member.getName()) &&
            StringUtils.hasText(member.getEmail())){
            return memberRepository.save(member);
        }else{
            return null;
        }
    }

    @Override
    public List<Member> findAll() {
        return List.of();
    }

    @Override
    public Member findById(Long id) {
        return null;
    }

    @Override
    public Member update(Long id, Member m) {
        Member findMember
                = memberRepository.findById(id).orElse(null);
        if( findMember == null
             || StringUtils.hasText(m.getName()) == false
             || StringUtils.hasText(m.getEmail()) == false){
            return null;
        }
        findMember.update(m.getName(), m.getEmail());
        return memberRepository.save(findMember);
    }

    @Override
    public boolean delete(Long id) {
        return false;
    }
}
