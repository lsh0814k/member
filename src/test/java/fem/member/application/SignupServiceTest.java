package fem.member.application;

import fem.member.domain.exception.ResourceExistException;
import fem.member.application.port.UuidHolder;
import fem.member.application.port.MemberRepository;
import fem.member.domain.Member;
import fem.member.domain.MemberCreate;
import fem.mock.FakeMailSender;
import fem.mock.FakeMemberRepository;
import fem.mock.FakeUuidHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class SignupServiceTest {
    private SignupService memberService;
    private MemberRepository memberRepository;
    private UuidHolder uuidHolder;

    @BeforeEach
    void init() {
        this.memberRepository = new FakeMemberRepository();
        this.uuidHolder = new FakeUuidHolder("aaaa-aaaa-aaaa");
        CertificationService certificationService = new CertificationService(new FakeMailSender(), "localhost:8080");
        memberService = new SignupService(memberRepository, uuidHolder, certificationService);
    }

    @Test
    @DisplayName("MemberCreate 를 이용하여 Member 를 생성할 수 있다.")
    void create_Member_from_MemberCreate() {
        // given
        MemberCreate memberCreate = MemberCreate.builder()
                .loginId("slee")
                .password("a123456")
                .nickname("lee")
                .build();

        // when
        Member result = memberService.signup(memberCreate);

        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getLoginId()).isEqualTo("slee");
        assertThat(result.getNickname()).isEqualTo("lee");
        assertThat(result.getCertificationCode()).isEqualTo("aaaa-aaaa-aaaa");
    }

    @Test
    @DisplayName("회원 가입할 때 동일한 이메일이 있으면 ResourceExistException 예외가 발생한다.")
    void signup_exist_email_ResourceExistException() {
        // given
        MemberCreate memberCreate = MemberCreate.builder()
                .loginId("slee")
                .password("a123456")
                .nickname("lee")
                .build();
        Member member = Member.create(memberCreate, uuidHolder.random());
        memberRepository.save(member);

        // expected
        assertThatThrownBy(() -> memberService.signup(memberCreate))
                .isInstanceOf(ResourceExistException.class);
    }
}