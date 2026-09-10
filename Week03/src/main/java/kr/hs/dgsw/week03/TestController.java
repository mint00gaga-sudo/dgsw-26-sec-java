package kr.hs.dgsw.week03;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
public class TestController {
    @GetMapping("/test")
    public String testMethod(){
        return "Get Hello World";
    }
    @PostMapping("/test")
    public String testMethod_post(){
        return "Post Hello World";
    }
    @PutMapping("/test")
    public String testMethod_put(){
        return "Put Hello World";
    }
    @DeleteMapping("/test")
    public String testMethod_delete(){
        return "Delete Hello World";
    }
    @PatchMapping("/test")
    public String testMethod_patch(){
        return "Patch Hello World";
    }
}
