package kr.hs.dgsw.week03;

public class History_01 {
    /**
     * 옵셔널, 람다, 스트림.
     * 옵셔널 생성 - ofNullable(null), empty(), of("hello");
     * 옵션널 사용 - get(), orElse("");
     *
     * 람다 () -> { }
     * Consumer - 매개변수 O, Return X
     * Supplier - X, O
     * Function - O, O
     * Predicate - O, boolean
     * Runnable - X, X
     *
     * Comsumner<Integer> con = x -> System.out.println(x)
     * Supplier <String> sup = () -> "Hello";
     * Function<String, String> func =  name -> "hello"+name;
     * Function<Integer, Integer> func01 =  x -> x*x;
     * Predicate<Integer> xxxx = pred -> pred > 5;
     * Runnable runn = () -> System.out.println("11");
     *
     * Integer method( String s ){
     *     return Integer.parseInt(s);
     * }
     * Function<String, Integer> func = s -> Integer.parseInt(s);
     * Function<String, Integer> func_01 = Integer::parseInt
     * System.out.println("as"); --> System.out::println
     *
     *IoC - 객체의 생성, 관리등의 제어권을 Spring Container가 담당하는거.
     *DI - 필요한 의존 객체를 Spring Container에서 주입받아 사용함.
     *  - @AutoWired
     *  - 필드주입, 메소드 주입, 생성자 주입.
     *
     *
     * */



}
