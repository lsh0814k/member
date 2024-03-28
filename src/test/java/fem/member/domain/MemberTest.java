package fem.member.domain;

import fem.member.domain.exception.CertificationCodeNotMatchedException;
import fem.member.domain.vo.UserRole;
import fem.member.domain.vo.MemberStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemberTest {
    @Test
    @DisplayName("MemberCreate 를 이용하여 Member 를 생성할 수 있으며 유저 상태는 PENDING 상태이며 유저의 권한은 USER 이다.")
    void create_member_status_equal_PEDNING_and_role_equals_USER() {
        // given
        MemberCreate memberCreate = MemberCreate.builder()
                .loginId("slee")
                .password("a123456")
                .nickname("lee")
                .build();
        String certificationCode = "aaaa-aaaa-aaaa";

        // when
        Member member = Member.create(memberCreate, certificationCode);

        // then
        assertThat(member.getLoginId()).isEqualTo("slee");
        assertThat(member.getRole()).isEqualTo(UserRole.USER);
        assertThat(member.getStatus()).isEqualTo(MemberStatus.PENDING);
        assertThat(member.getCertificationCode()).isEqualTo("aaaa-aaaa-aaaa");
    }

    @Test
    @DisplayName("유효한 인증 코드로 계정을 활성화 시킬 수 있다.")
    void activeMember_valid_certificationCode() {
        // given
        Member member = Member.builder()
                .id(1L)
                .loginId("slee")
                .nickname("lee")
                .password("a123456")
                .certificationCode("aaaa-aaaa-aaaa")
                .role(UserRole.USER)
                .status(MemberStatus.PENDING)
                .build();
        // when
        member.certificate("aaaa-aaaa-aaaa");

        // then
        assertThat(member.getStatus()).isEqualTo(MemberStatus.ACTIVE);
    }

    @Test
    @DisplayName("유효하지 않은 인증 코드로 계정을 활성화 하려하면 CertificationCodeNotMatchedException 예외가 발생한다.")
    void activeMember_invalid_certificationCode_CertificationCodeNotMatchedException() {
        // given
        Member member = Member.builder()
                .id(1L)
                .loginId("slee")
                .nickname("lee")
                .password("a123456")
                .certificationCode("aaaa-aaaa-aaaa")
                .role(UserRole.USER)
                .status(MemberStatus.PENDING)
                .build();
        // when
        // then
        assertThatThrownBy(() -> member.certificate("bbbb-bbbb-bbbb"))
                .isInstanceOf(CertificationCodeNotMatchedException.class);
    }
}