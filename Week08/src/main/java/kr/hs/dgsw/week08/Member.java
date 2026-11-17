package kr.hs.dgsw.week08;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@AllArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name="name", nullable = false)
    private String name;

    public Member(String name) {
        this.name = name;
    }

    /** 프로젝트에서 사용시, 호출하는 클래스에스
     * @Transactional  추가해야함. 그래야 DB의 값이 변경됨.*/
    public void updateMemberName(String newName){
        this.name = newName;
    }




    @Override
    public String toString() {
        return "id : "+id +" / name : "+name;
    }


}









