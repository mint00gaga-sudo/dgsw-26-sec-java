package kr.hs.dgsw.week03_01;

import jakarta.persistence.*;

/** 엔티티(Entity) 클래스,
 * 데이터베이스 테이블과 1:1 매칭. */

@Entity
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column( name="id", nullable = false)
    private Long id;

    @Column(name="name", nullable = false)
    private String name;

    public Member() {
    }
    public Member(Long id, String name) {
        this.id = id;
        this.name = name;
    }
    public Long getId() {
        return id;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}












