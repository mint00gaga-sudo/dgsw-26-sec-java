package dgsw.hs.kr.week02;

import org.springframework.stereotype.Component;

@Component
public class Car {  //객체가 자동으로 만들어짐, --> DI, IoC
    private DieselEngine engine;

    public Car(DieselEngine engine) {
        this.engine = engine;
    }

    public void run(){
        System.out.println("-------시동걸기 ---------");
        engine.start();
        engine.checkStatus();
        System.out.println("------- 연료채우기 ---------");
        engine.fuel();
        engine.start();
        engine.checkStatus();
        System.out.println("---------------------------");
    }
}
