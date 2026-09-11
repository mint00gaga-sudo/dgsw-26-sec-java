package kr.hs.dgsw.week03_01;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
 * */
@RestController
public class MemberController {
    @Autowired
    MemberService memberService;

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
}






