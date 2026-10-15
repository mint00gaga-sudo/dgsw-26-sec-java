package kr.hs.dgsw.week03_01;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * 2단계 - Mockito 서비스 테스트.
 * 짝퉁 서비스 왜? (Mock)
 * MemberService, MemberRepository
 * Service 로직이 정상이냐를 확인 --> DB가 정상이냐? XXX
 *  Repository를 Mock으로 구성.
 *
 * @ExtendWith(MockitoExtension.class) --> Mockito를 JUnit에 연결.
 * @Mock : 가짜 객체를 생성.
 * @InjectMocks : 위에서 만든 가짜를 객체에 주입해 준다.
 * */

@ExtendWith(MockitoExtension.class)
@DisplayName("MemberService 단위 테스트. ")
class MemberServiceImplTest {
    @Mock
    private MemberRepository memberRepository;/** 가짜 저장소*/

    @InjectMocks
    MemberServiceImpl memberService;/** 가짜를 주입받는 진짜 테스트 대상. */

    /**----------------------------------------------------------*/
    @Nested
    @DisplayName("getAllMembers() : 전체 조회. ")
    class GetAllMembers{
        @Test
        @DisplayName("저장소가 저장한 목록을 그대로 반환.")
        void returnsAll(){
            //given : DB 에다가 2명을 추가 - type01
//            Member member8 = new Member(8L, "달봉이8");
//            Member member9 = new Member(9L, "달봉이9");
//            memberRepository.save(member8);
//            memberRepository.save(member9);

            //when
            //List<Member> rsult = memberService.getAllMembers();

            //given
            List<Member> stub = List.of(new Member(8L, "달봉이8")
                        , new Member(9L, "달봉이9"));
            given(memberRepository.findAll()).willReturn(stub);
            //when
            List<Member> result = memberService.getAllMembers();

            //then
            assertThat(result)
                    .hasSize(2)
                    .extracting(Member::getName)
                    .containsExactly("달봉이8","달봉이9");

            verify(memberRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("데이터가 없으면 null, 빈값. ")
        void returnsEmptyList(){
            //given
            given(memberRepository.findAll()).willReturn(List.of());
            //when
            List<Member> result = memberService.getAllMembers();
            //then
            assertThat(result).isNotNull().isEmpty();
        }
    }


    @Nested
    @DisplayName("saveMember() : 저장, 이름 검증. ")
    class SaveMember{
        @Test
        @DisplayName("이름이 정상적인 경우, 저장하고 회원정보를 리턴.")
        void saveSuccess(){
            Member req = new Member( null, "달봉이");
            Member saved = new Member(1L, "달봉이");
            given(memberRepository.save(any(Member.class)))
                    .willReturn(saved);

            Member result = memberService.saveMember(req);
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getName()).isEqualTo("달봉이");
        }

        @Test
        @DisplayName("이름 앞뒤에 공백을 제거한다. trim(), 제거후 저장.")
        void trimNameSave(){
            given(memberRepository.save(any(Member.class)))
                    .willReturn(new Member(1L, "달봉이"));
            memberService.saveMember(new Member(null, "     달봉이       "));

            /** ArgumentCaptor : 디비에 실제로 넘어간 데이터를 낚아챈다.*/
            ArgumentCaptor<Member> captor
                    = ArgumentCaptor.forClass(Member.class);
            verify(memberRepository).save(captor.capture());
            assertThat(captor.getValue().getName()).isEqualTo("달봉이");
        }


        @ParameterizedTest(name="[{index}] 이름이 \"{0}\" 이면 저장안함.")
        @NullSource
        @EmptySource
        @ValueSource(strings = {" ","  ","\t","\n"})
        @DisplayName("이름이 비어 있으면, null을 반환하고 저장안함.")
        void rejectsBlankName(String invalidName){
            Member result
                    = memberService.saveMember(
                            new Member(null, invalidName));
            assertThat(result).isNull();
            verify(memberRepository, never())
                            .save(any(Member.class));
        }
    }
    /**== end class SaveMember  =======================*/


    @Nested
    @DisplayName("getMemberById() : 1명 조회")
    class GetMemberById{
        @Test
        @DisplayName("존재하는 id는 해당 회원 반환.")
        void found(){
            given(memberRepository.findById(1L))
                   .willReturn(Optional.of(new Member(1L, "달봉이")));
            Member result = memberService.getMemberById(1L);
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("달봉이");
        }

        @Test
        @DisplayName("없는 id면 예외가 아니라, null을 반환한다.")
        void notFound(){
            given(memberRepository.findById(999999L))
                    .willReturn(Optional.empty());
            Member result = memberService.getMemberById(999999L);
            assertThat(result).isNull();
        }
    }
    /**== end class GetMemberById  =======================*/

    @Nested
    @DisplayName("deleteMemberById : 삭제")
    class DeleteMemberById{

        @Test
        @DisplayName("존재하는 id면 삭제하고 true를 반환")
        void deleteSuccess(){
            given(memberRepository.existsById(1L)).willReturn(true);
            boolean result = memberService.deleteMemberById(1L);//true 리턴?
            assertThat(result).isTrue();

            verify(memberRepository, times(1)).deleteById(1L);  //진짜 삭제?
        }


        @Test
        @DisplayName("없는 id면 false리턴, 삭제시도도 안함.")
        void deleteFail(){

        }

    }








}