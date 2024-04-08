package fem.member.application;

import fem.member.application.port.MemberRepository;
import fem.member.domain.Member;
import fem.member.domain.exception.ResourceNotFoundException;
import fem.member.domain.vo.MemberStatus;
import fem.member.domain.vo.UserRole;
import fem.mock.FakeMemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import static org.assertj.core.api.Assertions.*;

class UserTokenServiceTest {

    private UserTokenService userTokenService;
    private MemberRepository memberRepository;

    @BeforeEach
    void init() {
        this.memberRepository = new FakeMemberRepository();
        this.userTokenService = new UserTokenService(memberRepository);
    }

    @Test
    @DisplayName("loginId 를 통해 UserDetails 를 생성할 수 있다.")
    void create_userDetails_by_loginId() {
        // given
        Member member = Member.builder()
                .id(1L)
                .loginId("slee@naver.com")
                .nickname("lee")
                .password("a123456")
                .certificationCode("aaaa-aaaa-aaaa")
                .role(UserRole.USER)
                .status(MemberStatus.ACTIVE)
                .build();
        memberRepository.save(member);

        // when
        UserDetails result = userTokenService.loadUserByUsername("slee@naver.com");

        // then
        assertThat(result.getUsername()).isEqualTo("slee@naver.com");
    }

    @Test
    @DisplayName("잘못된 loginId 가 전달 되면 ResourceNotFoundException 이 발생한다.")
    void userDetails_by_wrong_loginId() {
        // given
        // when
        // then
        assertThatThrownBy(() -> userTokenService.loadUserByUsername("slee@naver.com"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

}