import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

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