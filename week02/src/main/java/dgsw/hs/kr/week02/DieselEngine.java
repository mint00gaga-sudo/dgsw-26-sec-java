package dgsw.hs.kr.week02;

import org.springframework.stereotype.Component;

@Component
public class DieselEngine {
    public void start(){
        System.out.println("Diesel Engine started");
    }
    public void checkStatus(){
        System.out.println("Diesel Engine check status");
    }
    public void fuel(){
        System.out.println("Diesel Engine fuel");
    }
}
