package kr.hs.dgsw.week07_pratice.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table( name="members")
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@Getter
public class Member{
    /** id : BIGINT(Long), NULL(X), 자동증가 */
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;

    /**name: VARCHAR, 길이(50), NULL(X), 회원이름 */
    @Column(nullable = false, length = 50)
    private String name;

    /**eamil: VARCHAR, 길이(100), NULL(X), 회원 이메일
     * 중복안됨 - , unique = true*/
    @Column(nullable = false, length = 100)
    private String email;

    /**** 생성자는 name, email을
     * 매개변수로 받고 id는 생성자에서 초기화 하지 않음 */
    public Member(String name, String email){
        this.name = name;
        this.email = email;
    }

    /** 엔티티 수정시 이름, 이메일은 수정할 수 있으며
     * Setter 대신 별도의 수정메소드를 사용함. */
    public void update(String name, String email){
        this.name = name;
        this.email = email;
    }
}
