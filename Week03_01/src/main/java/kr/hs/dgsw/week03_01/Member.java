package kr.hs.dgsw.week03_01;

import jakarta.persistence.*;
import lombok.*;

/** 엔티티(Entity) 클래스,
 * 데이터베이스 테이블과 1:1 매칭. */

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Table(name="member_tbl")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column( name="id", nullable = false)
    private Long id;

    @Setter
    @Column(name="name", nullable = false, length = 50)
    private String name;

}












