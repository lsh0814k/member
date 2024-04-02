package fem.member.infrastructure.web.request;

import fem.member.domain.MemberCreate;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class SignupRequestTest {

    @Test
    @DisplayName("MemberCreate를 생성할 수 있다.")
    void create_memberCreate() {
        // given
        SignupRequest signupRequest = SignupRequest.builder()
                .loginId("slee@naver.com")
                .nickname("lee")
                .password("123456")
                .passwordConf("123456")
                .build();

        // when
        MemberCreate result = signupRequest.toModel();

        // then
        assertThat(result.getNickname()).isEqualTo("lee");
        assertThat(result.getLoginId()).isEqualTo("slee@naver.com");
        assertThat(result.getPassword()).isEqualTo("123456");

    }

}