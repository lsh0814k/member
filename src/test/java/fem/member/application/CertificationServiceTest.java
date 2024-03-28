package fem.member.application;

import fem.member.domain.Member;
import fem.member.domain.vo.MemberStatus;
import fem.member.domain.vo.UserRole;
import fem.mock.FakeMailSender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CertificationServiceTest {
    private final CertificationService certificationService;
    private final FakeMailSender mailSender;

    public CertificationServiceTest(@Value("${certification.host}") String host) {
        this.mailSender = new FakeMailSender();
        this.certificationService = new CertificationService(this.mailSender, host);
    }

    @Test
    @DisplayName("회원의 id와 certificationCode 를 이용하여 email 을 발송하는 템플릿을 생성한다.")
    void send_mail_create_template() {
        // given
        Member member = Member.builder()
                .id(1L)
                .loginId("slee@naver.com")
                .nickname("lee")
                .password("a123456")
                .certificationCode("aaaa-aaaa-aaaa")
                .role(UserRole.USER)
                .status(MemberStatus.PENDING)
                .build();

        // when
        certificationService.send(member.getLoginId(), member.getId(), member.getCertificationCode());

        // then
        assertThat(mailSender.getEmail()).isEqualTo("slee@naver.com");
        assertThat(mailSender.getSubject()).isEqualTo("이메일 인증");
        assertThat(mailSender.getText()).isEqualTo(
                "링크 클릭을 통해 가입을 완료해주세요: localhost:8080/api/members/1/verify?certificationCode=aaaa-aaaa-aaaa");
    }
}