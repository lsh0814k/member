package fem.member.infrastructure.web.response;

import fem.member.domain.Member;
import fem.member.domain.vo.MemberStatus;
import fem.member.domain.vo.UserRole;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static fem.member.domain.vo.MemberStatus.*;
import static fem.member.domain.vo.UserRole.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class MemberResponseTest {

    @Test
    @DisplayName("Member 를 통해 MemberResponse 를 생성할 수 있다.")
    void create_MemberResponse() {
        // given
        Member member = Member.builder()
                .id(1L)
                .loginId("slee@naver.com")
                .nickname("lee")
                .password("a123456")
                .certificationCode("aaaa-aaaa-aaaa")
                .role(USER)
                .status(PENDING)
                .build();

        // when
        MemberResponse result = MemberResponse.from(member);

        // then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getNickname()).isEqualTo("lee");
        assertThat(result.getRole()).isEqualTo(USER);
        assertThat(result.getStatus()).isEqualTo(PENDING);
    }

}