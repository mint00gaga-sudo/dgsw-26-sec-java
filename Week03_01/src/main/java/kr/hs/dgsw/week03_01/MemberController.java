package kr.hs.dgsw.week03_01;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** 클라이언트 url에 맵핑되는 메소드를 호출.
 * @RequestParam
 * @PathVariable
 * @RequestBody
 * @ModelAttribute
 * @RequestPart
 * @ReqeustHeader
 *
 *
 * 생성자란 ?
 *  * - 클래스를 생성하는거.(new)
 *  * - 자동으로 호출되는 놈.
 *  * - 클래스와 이름이 같다.
 *  * - 리턴형이 없다.
 *
 *  final - 끝.
 *  변수 - 할당후 변경 불가.
 *  메소드 - 오버라이딩 불가
 *  클래스 - 상속불가.
 *
 * */
@RestController
@RequiredArgsConstructor
public class MemberController {
    final MemberService memberService;
    final MemberRepository memberRepository;
    final MemberRepository memRep;
    int size;

    @GetMapping("/member")
    public List<Member> getAllMembers() {
        List<Member> members = memberService.getAllMembers();
        return members;
    }

    /** 멤버추가. */
    @PostMapping("/member")
    public Map<String, Object>
            addMember(@RequestBody Member member) {
        /** 회원정보 입력 --> 서버에 전송 --> 디비에 저장 --> 응답.*/
        Map<String, Object> res = new HashMap<>();
        Member member1 = memberService.saveMember(member);

        if( member1 == null){
            /** 데이터가 비정상*/
            res.put("res", "fail");
            res.put("msg", "not value: [name]");
        }else{
            /** 정상 데이터. */
            res.put("res", "success");
            res.put("msg", member1);
        }
        return res;
    }

    /** 회원 1명 조회. */
    @GetMapping("/member/{id}")
    public Map<String, Object> getSingleMember(@PathVariable("id") Long userId) {
        Map<String, Object> res = new HashMap<>();
        Member foundMember = memberService.getMemberById( userId );

        if(foundMember == null){
            res.put("res", "fail");
            res.put("msg", "not Member userId : "+userId);
        }else{
            res.put("res", "success");
            res.put("data", foundMember);
        }

        return res;

    }


    /** 단일 삭제, Http Method 는 Delete로 작성.
     * Controller, Servive 메소드. */
    /** 회원 1명 삭제. */
    @DeleteMapping("/member/{id}")
    public Map<String, Object> deleteMember(@PathVariable("id") Long userId) {
        Map<String, Object> res = new HashMap<>();
        boolean isDelete = memberService.deleteMemberById( userId );

        if(isDelete == false){
            res.put("res", "fail");
            res.put("msg", "not Member userId : "+userId);
        }else{
            res.put("res", "success");
            res.put("data", "delete complete");
        }
        return res;
    }
}














