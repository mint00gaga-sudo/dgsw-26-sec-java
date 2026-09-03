import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        Stream<Integer> intStream = list.stream();
        Stream.of(new String[]{"a","b","c"}, "d");    //스트링배열 a,b,c 가 들어간, new 사용해서.
        Stream.of("a");
    }
}