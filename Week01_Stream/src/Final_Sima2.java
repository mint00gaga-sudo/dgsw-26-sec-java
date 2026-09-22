import java.security.cert.X509Certificate;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Optional, 람다, 메소드 참조, 스트림
 * 스프링 빈, IoC( 제어의 역전 ), DI( 의존성 주입 ), AOP, PSA
 * 스프링 MVC - RestContoller, Service, Repository, --> 빈(Bean)
 * 빈만들어 --> Component
 *
 *
 * */
public class Final_Sima2 {
   int aa(int x) {
       return x * 2;
   }
   Function<Integer, Integer> func01 = (x) -> x*2;
//   서플라이어 - Supplier --> 매변수 X, 리:O
//   Runnable - 매:X, 리:X
//   Consumer - 매:O, 리:X
//   Predicate - 매:O, 리:boolean

    /** Optional
     * 꺼내기 - get() --> NullPointerException, ofElse("기본값")
     * System.out::println --> s -> System.out.println(s);
     * String::length --> n -> n.length()
     * Integer::parseInt --> x -> Integer.parseInt(x)
     * */

    /**스트림
     * 중복제거 - distinct()
     * filter( 조건 ) --> 제거 조건이 true면 남음.
     * map( 변환식 ) --> 변경.
     * 정렬 - sorted()
     *
     *
     * 최종연산
     * forEach( 람다 )
     * count - 개수, Long
     *
     * 3보다 큰수만 골라서( filter) 10을 곱한다음에(map) 출력(forEach).
     * nums.strem()
     *  .filter( n -> n > 3 )
     *  .map( n  -> n * 10)
     *  .forEach( System.out::println)
     *
     *  filter ( 람다 ) --> true만 남음
     *  removeIf( 람다 ) --> true만 삭제,
     * */


    /**
     * 스프링 부트.
     * 빈 - IoC(제어의 역전), DI(의존성 주입, @AutoWired) , AOP, PSA
     * DI - 필드, 메소드, 생성자.
     *
     * @Component, @Service, @RestController, @Repository.
     *
     * /* 빈으로 등록
     * @Service  --> @Component
     * class MyService{
     *
     * }
     *
     * @GetMapping("/member")
     * @PostMapping("/member")
     *
     *
     * 데이터 받기
     * @PathVariable,
     * @RequestBody
     * @RequestParam
     * */



}

@RestController
class MyController{

    @PostMapping("/member")
    public String myMethod(){

    }
}






