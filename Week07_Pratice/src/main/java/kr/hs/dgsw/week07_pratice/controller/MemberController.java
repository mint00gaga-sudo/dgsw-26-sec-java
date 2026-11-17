package kr.hs.dgsw.week07_pratice.controller;

import kr.hs.dgsw.week07_pratice.domain.Member;
import kr.hs.dgsw.week07_pratice.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MemberController {
    final private MemberService memberService;

    /**
     *
     * [요청]
     * GET /member
     * [성공 응답]
//     * [
//     *   { "id": 1, "name": "달봉이" },
//     *   { "id": 2, "name": "홍길동" },
//     *   { "id": 3, "name": "김유신" },
//     *   { "id": 4, "name": "이순신" }
//     * ]
     * [실패 응답]
     * 없음 (회원이 없으면 빈 배열 [] 을 반환함)*/

    @GetMapping("/member")
    public List<Member> getMember(){
        return memberService.findAll();
    }
    /**
     * [요청]
     * GET /member/{id}
     * [성공 응답]
     * {
     *   "res": "success",
     *   "data": { "id": 1, "name": "달봉이" }
     * }
     * [실패 응답]  해당 id의 회원이 없는 경우
     * {
     *   "res": "fail",
     *   "msg": "not Member userId : 999"
     * }
     * */
    @GetMapping("/member/{id}")
    public Map<String, Object> fineOne(@PathVariable Long id){
        Map<String, Object> returnValue = new HashMap<>();
        Member mem = memberService.findById(id);
        if( mem != null){
            /** 여기는 멤버가 있어. */
            returnValue.put("res", "success");
            returnValue.put("data", mem);
        }else{
            /** 멤버가 없어. */
            returnValue.put("res", "fail");
            returnValue.put("data", "not Member userId : "+id);
        }
        return returnValue;
    }

    /**
     * [요청]
     * DELETE /member/4
     * [성공 응답]
     * {
     *   "res": "success",
     *   "data": "delete complete"
     * }
     * [실패 응답]  해당 id의 회원이 없는 경우
     * {
     *   "res": "fail",
     *   "msg": "not Member userId : 999"
     * }
     * */
    @DeleteMapping("/member/{userid}")
    public Map<String, Object> deleteMember
    (@PathVariable("userid") Long userid) {
        boolean isDeleted = memberService.delete(userid);
        Map<String, Object> resultValue = new HashMap<>();
        if( isDeleted ){
            resultValue.put("res", "success");
            resultValue.put("data", "delete complete");
        }else{
            resultValue.put("res", "fail");
            resultValue.put("data", "not Member userId : "+userid);
        }
        return resultValue;

        /**
         * ResponseEntity
         * 200 - 성공, 정상. ResponseEntity.ok()
         * 201 - 입력,
         * 400 - 파라미터 잘못,
         * 404 - 페이지 없음, url 잘못입력.
         *
         *
         * GET /member/{id}
         * 성공 : 200, ......
         * 실패 : 400... ...
         * */


    }

    /** 전체조회
     * 전체 회원 조회  GET    │ /api/members
     * parameter : 없음.
     * 응답 : 200 OK
     * [성공 응답] "data"
     * //     * [
     * //     *   { "id": 1, "name": "달봉이" },
     * //     *   { "id": 2, "name": "홍길동" },
     * //     *   { "id": 3, "name": "김유신" },
     * //     *   { "id": 4, "name": "이순신" }
     * //     * ]
     * [실패 응답]
     * 없음 (회원이 없으면 빈 배열 [] 을 반환함)*/
    @GetMapping("/api/members")
    public ResponseEntity<Map<String, Object>> getAllMember(){
        Map<String, Object> resultValue = new HashMap<>();
        List<Member> all = memberService.findAll();
        resultValue.put("res", "success");
        resultValue.put("data",all );

        return ResponseEntity.ok(resultValue);
    }

    /** 성공 : 200, 실패, 404
     * */
    @GetMapping("/api/member/{id}")
    public ResponseEntity< Map<String, Object>> fineOneMember(@PathVariable Long id){
        Map<String, Object> returnValue = new HashMap<>();
        Member mem = memberService.findById(id);
        if( mem != null){
            /** 여기는 멤버가 있어. */
            returnValue.put("res", "success");
            returnValue.put("data", mem);
            return ResponseEntity.ok(returnValue);

        }else{
            /** 멤버가 없어. */
            returnValue.put("res", "fail");
            returnValue.put("data", "not Member userId : "+id);
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(returnValue);
        }

    }


    /** 수정
     * PUT , 성공, 200,  실패 400,
     * URI : api/member/{id} */
    @PutMapping("/api/member/{id}")
    public ResponseEntity<Map<String, Object>>
            updateMember( @PathVariable("id") Long id,
                          @RequestBody Member member){

        Member updateMember = memberService.update(id, member);
        Map<String, Object> resultValue = new HashMap<>();

        if( updateMember == null){
            resultValue.put("res", "fail");
            resultValue.put("data","회원정보 수정을 실패");
            return ResponseEntity
                    .badRequest()
                    .body(resultValue);
        }
        resultValue.put("res", "success");
        resultValue.put("data", updateMember);
        return  ResponseEntity.ok(resultValue);
    }

    /** 등록
     * 성공 - 201 , 실패 - 400
     * Parameter : Member
     * url : /api/member
     * Method : POST */
    @PostMapping("/api/member")
    public ResponseEntity<Map<String, Object>>
            createMember(@RequestBody Member member){
        Member createMember = memberService.create(member);
        Map<String, Object> resultValue = new HashMap<>();
        if( createMember == null){
            resultValue.put("res", "fail");
            resultValue.put("data", "이름 혹은 이메일이 없습니다.");
            return ResponseEntity.badRequest().body(resultValue);
        }
        resultValue.put("res", "success");
        resultValue.put("data", createMember);
        return ResponseEntity.ok(resultValue);
    }
    /**
     * 삭제
     * Method : DELETE
     * URI : /api/member/{id}
     * 성공 - 200, 실패 - 404
     * 
     * */
}










