package kr.hs.dgsw.week09;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@ToString
public class Course {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;
    private  String name;
    private String category;
    private int rating;
    private String description;

    public Course(String name, String category, int rating, String description) {
        this.name = name;
        this.category = category;
        this.rating = rating;
        this.description = description;
    }
}
