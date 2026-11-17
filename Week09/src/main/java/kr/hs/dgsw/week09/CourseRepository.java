package kr.hs.dgsw.week09;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository
        extends JpaRepository<Course, Long> {
    /** 이름 또는 카테고리로 검색 - 쿼리 메소드보다 조금더 정교하게, 자바스럽게.
     * SELECT * FROM COURSE WHERE name='z' OR category='b'*/
    @Query("SELECT c FROM Course c WHERE " +
            "c.name= :name OR c.category= :category")
    List<Course> findByNameOrCategory(
            @Param("name") String name,
            @Param("category") String category);

    /** 특정 카테고리안에 평점이 얼마 이상인것만 조회 */
    @Query("SELECT c FROM Course c " +
            "WHERE c.category= :category AND " +
            "c.rating >= :rating")
    List<Course> findByCategoryAndMinRating(
            @Param("category") String category,
            @Param("rating") int rating
    );

    /** SQL문을 그대로 작성하기
     * 카테고리가 동일한거 찾기 */
    @Query(value = "SELECT * FROM course WHERE category = :course"
    , nativeQuery = true)
    List<Course> findByCategoryNative(
            @Param("course") String course
    );
}









