package kr.hs.dgsw.week04;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 테스트
 *
 * JUnit4.
 *
 * 테스트 툴
 * JUnit
 *  - 자바를 위한 단위테스트 프레임워크
 *  - 단위테스트 - 코드가 정상동작하는지 메소드 단위로 검증.
 * 특징
 *  - 테스트방식에 따른 어노테이션 제공.
 *  - @Test 메소드마다 테스트진행, 독립적, 메소드 1개를 위한 인스턴스 생성
 *  - 예상결과 어설션 클래스 제공.
 *  -  경량, 가볍고, 빠르고, 즉각적인 피드백.
 *
 *
 * AssertJ - 검증문 작성하는 LIB.
 * Mockito - 가상.
 *
 * */
public class TestCode_01 {

    @Test
    @DisplayName("1+2는 3이야.")
    public void test01(){
        int a = 1;
        int b = 2;
        int c = 3;
        //import org.junit.jupiter.api.Assertions;
        Assertions.assertEquals(c, a+b);
    }

    @Test
    @DisplayName("오류테스트")
    public void test02(){
        int a = 1;
        int b = 2;
        int c = 5;
        //import org.junit.jupiter.api.Assertions;
        Assertions.assertEquals(c, a+b);
    }
}










