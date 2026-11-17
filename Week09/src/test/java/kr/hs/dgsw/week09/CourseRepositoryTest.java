package kr.hs.dgsw.week09;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.map;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CourseRepositoryTest {
    @Autowired
    private CourseRepository cRep;

    @Test
    @Sql("/insert-courses.sql")
    void findByNameOrCategory(){
        List<Course> result = cRep.findByNameOrCategory(
                "Java Fundamentals",
                "Python"
        );
        assertThat(result)
                .extracting(Course::getCategory)
                .contains("Java", "Python");
    }

    @Test
    @Sql("/insert-courses.sql")
    void findByCategoryAndMinRating(){
        List<Course> result
                = cRep.findByCategoryAndMinRating("Java", 5);
        assertThat( result )
                .isNotEmpty()
                .allSatisfy( c -> {
                    assertThat(c.getCategory()).isEqualTo("Java");
                    assertThat(c.getRating()).isGreaterThanOrEqualTo(5);
                });
    }
}









