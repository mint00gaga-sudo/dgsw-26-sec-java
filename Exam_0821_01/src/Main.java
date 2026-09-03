import java.util.Optional;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        Optional.ofNullable(null).ifPresent( n -> System.out.println(n));
        Optional<String> drink = Optional.ofNullable(null);
        System.out.println(drink.orElse("물"));
        String math01 = getExamScore("수학");
        Optional<String> math2 = Optional.ofNullable(math01);
        System.out.println(math2.orElse("0"));

        Optional<String> opt01 = Optional.ofNullable("자바");
        opt01.ifPresent( v -> System.out.println("값있음"));

        Optional<String> login = Optional.ofNullable("성공");//실패 null

        System.out.println(
                login.isPresent() ? "로그인 성공" : "로그인 실패");
        Optional<String> name = Optional.ofNullable("달봉이");
        System.out.println(
                name.isPresent() ? "합격 ["+name+"]" : "불합격" );

    }
    /** 연습문제 6번.*/
    static String getExamScore(String question) {
        return null;
    }
}




















