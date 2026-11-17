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
public class Member_V2 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String pwd;
    private String phone;
    /**
     * 회원가입 - 이름, 이메일, 비밀번호, 전화번호
     * 전화번호로 이메일찾기
     *  - 요청:전화번호(MemberRequest_SearchEmail_DTO),
     *  - 응답:이메일(MemberResponse_SearchEamil_DTO)
     * 이름, 이메일 변경 :
     *  - 요청:이름,이메일 ( MemberRequest_UpdateInfo_DTO)
     *  - 응답:이름,이메일,전화번호 (MemberResponse_UpdateInfo_DTO)
     * */
    //public MemberRequest_UpdateInfo_DTO  toMemberResUpdate_DTO(){

   // }


    public Member_V2(String name) {
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









