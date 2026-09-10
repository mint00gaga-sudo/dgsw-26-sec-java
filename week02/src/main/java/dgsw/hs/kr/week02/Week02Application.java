package dgsw.hs.kr.week02;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class Week02Application {
	public static void main(String[] args) {
		ApplicationContext context =
			SpringApplication.run(Week02Application.class, args);

		Car car = context.getBean(Car.class);
		car.run();

	}
}
