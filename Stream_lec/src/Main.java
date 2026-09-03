//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * 스트림, 자료(자료구조) 를 표준화 해서 다룬기.
 * 1.컬렉션 - Collection (Set, Map, List )
 * 2.다됨.
 *
 * 생성 --> 중간연산 --> 최종연산
 *
 * 데이터(원본) --> 스트림생성 ( 복사본 ) --> 중간중간중간 연산자 --> 최종연산(종료)
 *
 * */
public class Main {
    public static void main(String[] args) {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        Stream<Integer> intStream = list.stream();
        Stream.of(new String[]{"a","b","c"}, "d");    //스트링배열 a,b,c 가 들어간, new 사용해서.
        Stream.of("a");
    }
}









