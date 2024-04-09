package fem.member.application;

import fem.member.application.port.MemberRepository;
import fem.member.domain.Member;
import fem.member.domain.exception.CertificationCodeNotMatchedException;
import fem.member.domain.exception.ResourceNotFoundException;
import fem.member.domain.vo.UserRole;
import fem.mock.FakeMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static fem.member.domain.vo.MemberStatus.ACTIVE;
import static fem.member.domain.vo.MemberStatus.PENDING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemberServiceTest {
    private MemberService memberService;
    private MemberRepository memberRepository;

    @BeforeEach
    void init() {
        this.memberRepository = new FakeMemberRepository();
        this.memberService = new MemberService(memberRepository);
    }

    @Test
    @DisplayName("인증할 때 유효한 id와 유효한 인증번호로 회원을 인증하고 status 값을 active 로 변경한다.")
    void verify_valid_id_and_certification() {
        // given
        Member member = Member.builder()
                .loginId("slee@naver.com")
                .nickname("lee")
                .password("a123456")
                .certificationCode("aaaa-aaaa-aaaa")
                .role(UserRole.USER)
                .status(PENDING)
                .build();
        memberRepository.save(member);

        // when
        memberService.verifyEmail(1L, "aaaa-aaaa-aaaa");

        // then
        Member result = memberRepository.getById(1L);
        assertThat(result.getStatus()).isEqualTo(ACTIVE);
    }

    @Test
    @DisplayName("인증할 때 유효하지 않은 id로 인증하려 하면 ResourceNotFoundException 오류가 발생한다.")
    void verify_invalid_id() {
        // given
        // when
        // then
        assertThatThrownBy(() -> memberService.verifyEmail(1L, "aaaa-aaaa-aaaa"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("인증할 때 이미 인증된 id로 인증하려 하면 ResourceNotFoundException 오류가 발생한다.")
    void verify_already() {
        // given
        // when
        Member member = Member.builder()
                .loginId("slee@naver.com")
                .nickname("lee")
                .password("a123456")
                .certificationCode("aaaa-aaaa-aaaa")
                .role(UserRole.USER)
                .status(ACTIVE)
                .build();
        memberRepository.save(member);

        // then
        assertThatThrownBy(() -> memberService.verifyEmail(1L, "aaaa-aaaa-aaaa"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("인증할 떄 유효하지 않은 인증코드로 인증하면 CertificationCodeNotMatchedException 오류가 발생한다")
    void verify_invalid_certificationCode() {
        // given
        // when
        Member member = Member.builder()
                .loginId("slee@naver.com")
                .nickname("lee")
                .password("a123456")
                .certificationCode("aaaa-aaaa-aaaa")
                .role(UserRole.USER)
                .status(PENDING)
                .build();
        memberRepository.save(member);

        // then
        assertThatThrownBy(() -> memberService.verifyEmail(1L, "bbbb-bbbb-bbbb"))
                .isInstanceOf(CertificationCodeNotMatchedException.class);
    }
}