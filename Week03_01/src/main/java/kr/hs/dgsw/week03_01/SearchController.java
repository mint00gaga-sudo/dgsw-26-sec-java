package kr.hs.dgsw.week03_01;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


/**
 * http 요청데이터 수신 종류
 * @RequestParam  **
 * @RequestBody   **
 * @PathVariable  **
 *
 * @RequestPart
 * @RequestHeader
 * @ModelAttribute
 * @CookieValue
 *
 *
 *
 * */
@RestController
public class SearchController {

    /** @RequestParam
     * localhost:9090/search?keyword=노트북&page=3   */
    @GetMapping("/search")
    public String searchRequestParam(
            @RequestParam String keyword,
            @RequestParam int page
    ){
        return "키워드 : "+keyword + "는 [ "+page+"] 에 있음.";
    }

    /** @PathVariable 경로에 변수없이 데이터를 바로 입력.
     * 테스트 : localhost:9090/user/5      */
    @GetMapping("/user/{id}")
    public String getUserInfo(@PathVariable("id") Long id){
        return "사용자의 회원번호는 : "+id+" 입니다.";
    }

    /** @RequestBody */
    @PostMapping("/danggun")
    public String notiDanggun(@RequestBody DangGun newDanggun){
        return newDanggun.toString();
    }
}

@Getter
@Setter
@ToString
class DangGun{
    private String name;
    private String phone;
    private int price;
    private String si;
    private String gu;

}






