import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;


/**
 * 생성 --> 중간연산자 --> 최종연산자.
 * 중복제거 - distinct()
 * 정렬해라 - sorted()
 * 조건에 맞는것만 남겨 - filter
 * 데이터 변경 - map
 *
 *
 *
 * */
public class Main {
    public static void main(String[] args) {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        Stream<Integer> intStream = list.stream();
        Stream.of(new String[]{"a","b","c"}, "d");    //스트링배열 a,b,c 가 들어간, new 사용해서.
        Stream.of("a");

        List<Student> students = List.of(
                new Student(2, 250, "달봉이"),
                new Student(1, 300, "이순신"),
                new Student(2, 170, "김유신"),
                new Student(1, 270, "최지우"),
                new Student(3, 370, "정해인"),
                new Student(1, 350, "한소희")
        );
        Stream<Student> stream = students.stream();
        stream.sorted(
                Comparator.comparing(Student::getBan)
                        .thenComparing(Student::getTotalScore)
                )
                .forEach(t -> System.out.println(t));


        //1.
        //문제: 숫자 리스트에서 짝수만 골라낸 후(filter), 각 숫자에 10을 곱하세요.(map)

        List<Integer> num01 = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        num01.stream()
                .filter( n -> n % 2 == 0 )  //짝수만 남음.
                .map(n -> n * 10)
                .forEach(s -> System.out.println(s));
        //forEach(System.out::println );

        /** 사람리스트에서 김씨만 골라내고, 이름의 길이를 구하고, 내림차순으로 출력하세요.*/
        List<String> names
                = Arrays.asList("김민준","박서준","김하은","이도현","김서연");
        names.stream()
                .filter( n -> n.startsWith("김"))
                .map(String::length)
                .sorted( Comparator.reverseOrder() )
                .forEach(System.out::println);

        /** 학생들의 점수 리스트에서 50점 이상인 점수만 골라내고, 중복된 점수는 제거한 후
         * 내림차순으로 정렬하여 출력하세요. */
        List<Integer> score = Arrays.asList(70,55,90,80,50,55,60,90);
        score.stream()
                .filter( n -> n >= 50)
                .distinct()
                .sorted(Comparator.reverseOrder())
                .forEach(System.out::println);

    }

    static class Student {
        private int ban;
        private int totalScore;
        private String name;

        public Student(int ban, int totalScore, String name) {
            this.ban = ban;
            this.totalScore = totalScore;
            this.name = name;
        }

        public int getBan() {
            return ban;
        }
        public int getTotalScore() {
            return totalScore;
        }
        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return String
                .format("%d 반 %s ( 총점 : %d)",ban, name,totalScore);
        }
    }
}