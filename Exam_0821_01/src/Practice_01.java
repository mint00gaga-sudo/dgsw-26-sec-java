import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.*;

public class Practice_01 {
    public static void main(String args[]){
        MyFunction my01 = new MyFunction(){
            @Override
            public int max(int a, int b) {
                return a > b ? a : b;
            }
            //(a, b) -> a > b ? a:b;
        };
        int max01 = my01.max(1,2);
        MyFunction my02 = (a, b) -> a > b ? a:b;


        List<String> list = Arrays.asList("a", "z", "c", "l", "e", "x", "g", "h", "i", "j");
        Collections.sort(list, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareTo(s2);
            }
        });
        Collections.sort(list, (s1, s2) -> s1.compareTo(s2));

        MyFunction my05 = (s1, s2) -> s1*s2;
        MyFunction my06 = (s1, s2) -> {return s1+s2;};
        my05.max(5,6);
        Consumer<String> my07 = (s1) -> System.out.println(s1);
        Consumer<String> my08 = s1 -> System.out.println(s1);
        //Supplier<String> my09 = () -> 3;
        IntBinaryOperator ex01 = (a,b) -> a>b?a:b;
        int num01 = ex01.applyAsInt(1,2);

        BiConsumer<String, Integer> bicon01
                = (name, i) -> System.out.println(name+"="+i);
        bicon01.accept("자바", 5);

        IntUnaryOperator intU01 = x -> x*x;
        intU01.applyAsInt(5);
        Supplier<Integer> sup01 = () -> (int)(Math.random()*6);
        IntSupplier sup02 = () -> (int)(Math.random()*6);

        //5번 부터 다시 진행
     //ㅣㅏ미ㅏㅁ = (int n) -> {return n % 2; };
     IntPredicate isEven = n -> n % 2 == 0;
        isEven.test(5);

     Function<String, String> greet
                    = name -> "Hello, "+name+"!";
        greet.apply("달봉이");

        ToIntFunction<int[]> sumAll  = arr -> {
            int sum = 0;
            for (int n : arr) sum += n;
            return sum;
        };
        int sum = sumAll.applyAsInt(new int[]{1,2,3,4,5});
        //sum은 결과값은?

        BiFunction<String, Integer, String> repeat = ( s, n)->{
            String result = "";
            for( int i = 0; i < n; i++ ) result += s;
            return result;
        };
        repeat.apply("result", 5);

        Comparator<String>  compareLength
                = (s1, s2) -> s1.length() - s2.length();

        Predicate<String> isValidPassword = (pw) ->{
            if (pw == null || pw.length() < 8)  return false;
            for (char c: pw.toCharArray()) {
                if (Character.isDigit(c)) return true;
            }
            return false;
        };

        /**
         * 리턴형(A, 무조건 1개) 함수이름 ( 매개변수(B, 1개이상) ) { 구현 부 }
         * A, B둘다 없거나 - Runable, run()
         * A만 있거나 - Supplier
         * B만 있거나 - Consumer (소비만 하는 놈)
         * A,B 둘다 있거나. - Function
         *
         * Predicate - 판단하는 놈.
         * Comparator - 비교하는 놈.
         * Operator - 계산하는놈 ( 숫자 - int 전용 )
         *
         * */

        Predicate<String> testPre = s -> s.length() >= 8;
        String s = "";

        if(testPre.test(s)){
            //8자 이상입니다.
        }else{
            //8자 이하입니다.
        }
/**
        매개변수,  리턴형
       1. X        O --> Supplier
       2. X,     X   --> Runnable
       2.1 O,    X --> Consumer
       3. O,    O --> Function, Predicate
       4. O,    O --> function
        */



      List<Integer> _arrList
              = Arrays.asList(1,2,3,4,5,6,7,8,9,10);
        //3. 모든 요소에 10을 곱한뒤에  출력하세요.
        _arrList.replaceAll( x -> x*10 );
        _arrList.forEach( value ->  System.out.println(value) );

        for(int i=0 ; i<_arrList.size() ; i++){
            System.out.println(_arrList.get(i)*10);
        }


      //2. 2의배수 또는 3의 배수는 삭제하고 출력.
        for(int i=0 ; i<_arrList.size() ; i++){
            if( _arrList.get(i)%2 != 0 && _arrList.get(i)%3 != 0) {
                System.out.println(_arrList.get(i));
            }
        }
//        _arrList.removeIf( x -> x%2 == 0 || x%3 == 0   );
//        _arrList.forEach( value ->  System.out.println(value) );


      //전체 출력하세요.
      _arrList.forEach( value ->  System.out.println(value) );

      for(int i=0 ; i<_arrList.size() ; i++){
          System.out.println(_arrList.get(i));
      }
      int i=0;
      while (true){
          System.out.println(_arrList.get(i));
          if( i >= _arrList.size() ) break;
      }




        Function<String, Integer> f1
                    = (s1) -> Integer.parseInt(s1, 16);
        Function<Integer, String> g1
                    = (i1) -> Integer.toBinaryString(i1);
        Function<String, String> h1 = f1.andThen(g1);  // f + g
        h1.apply("FF");

        Function<Integer, String> f2
                 = (s2) -> Integer.toBinaryString(s2);  //2진수로 바꿈.
        Function<String, Integer> g2
                 = (i2) -> Integer.parseInt(i2, 16);    //16진수
        Function<Integer, Integer> h2 = g2.compose(f2);
        System.out.println(h2.apply(2)); //--> 10 --> ??

        /** f.andThen(g)  f --> g
         * f.compose(g) g --> f 실행순서.  */

        Predicate<Integer> p10 = i10 -> i10 < 100;
        Predicate<Integer> p11 = i11 -> i11 < 200;
        Predicate<Integer> p12 = i12 -> i12 % 2 == 0;

        Predicate<Integer> notP = p10.negate();     // i >= 100
        Predicate<Integer> allP = notP.and(p11).or(p12);
                        // 100 <= i && i < 200 || i%2 == 0
        Predicate<Integer> allNotP = notP.and(p11.or(p12));

        System.out.println(allP.test(2));
        System.out.println(allNotP.test(2));


        Function<String, Integer> func20
                 = (String s1) -> Integer.parseInt(s1,10);
        Function<String, Integer> func21
                = Integer::parseInt;

        BiFunction<String, String, Boolean> func22
                = (s22, s23) -> s22.equals(s23);
        BiFunction<String, String, Boolean> func23
                = String::equals;

        Object obj01 = new Object();
        Predicate<Object> obj02 = (x) -> obj01.equals(x);
        Predicate<Object> obj03 = obj01::equals;


        Supplier<MyClass> myClass01 = () -> new MyClass();
        Supplier<MyClass> myClass02 = MyClass::new;

        Function<Integer, MyClass> myClass03
                        = (myX01) -> new MyClass(myX01);
        Function<Integer, MyClass> myClass04
                        = MyClass::new;

        Function<Integer, int[]> arr01 = size -> new int[size];
        Function<Integer, int[]> arr02 = int[]::new;

        Function<Integer, Integer> fun44 = x -> Math.abs(x);
        Function<Integer, Integer> fun45 = Math::abs;

        Consumer<String> con01 = x-> System.out.println(x);
        Consumer<String> con02 = System.out::println;
        con02.accept("3");





//        (x) -> new MyClass(x);
//        MyClass::new;


    }
    static Integer methodA(String s1){
        return Integer.parseInt(s1);
    }


}//end Pratice_01 clas

@FunctionalInterface
interface MyFunction{
    int max(int a, int b);
}

class MyClass{
    public MyClass(){}
    public MyClass(int a){}
}












