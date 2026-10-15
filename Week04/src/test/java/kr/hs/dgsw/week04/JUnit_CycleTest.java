package kr.hs.dgsw.week04;

import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;

public class JUnit_CycleTest {
    @BeforeAll
    public static void beforeAll(){
        System.out.println("@BeforeAll");
    }
    @AfterAll
    public static void afterAll(){
        System.out.println("@AfterAll");
    }
    @BeforeEach
    public void beforeEach(){
        System.out.println("@BeforeEach");
    }
    @AfterEach
    public void afterEach(){
        System.out.println("@AfterEach");
    }

    @Test
    public void test01(){ System.out.println("test01"); }
    @Test
    public void test02(){
        System.out.println("test02");
        //양수 - Positive
        //Zero
        //음수 - Negative

        int num01 = 15;
        int num02 = 0;
        int num03 = -5;
        /** 1. num01이 양수인지 확인
         * 2. num02가 0인지 확인.
         * 3. num03이 음수인지 확인.
         * */

    }
    @Test
    public void test03(){
        System.out.println("test03");

        String str01 = "abcde";
        assertThat(str01).isNotNull();
        assertThat(str01).isEqualTo("abcde");
        assertThat(str01).isNotEqualTo("123abcde");
    }
}

/**
 * BeforeAll
 *  - 모든 테스트가 실행되기전 한번만 실행 - static
 *  - 공통리소스 초기화 - DB관련 리스소들.
 *
 *  BeforEach
 *   - 메소드가 실행될때마다 바로전에 1회 실행.
 *
 *   AfterAll
 *    - 모든테스트가 종료되고 딱 한번 실행 - static
 *    리소스 반납.
 *
 *   AfterEach
 *    - 메소드가 종료될때 마다 실행.
 *
 * */











