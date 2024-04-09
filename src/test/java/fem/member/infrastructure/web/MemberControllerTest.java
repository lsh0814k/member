package fem.member.infrastructure.web;

import fem.member.application.port.MemberRepository;
import fem.member.domain.Member;
import fem.member.domain.vo.MemberStatus;
import fem.member.domain.vo.UserRole;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static fem.member.domain.vo.MemberStatus.PENDING;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MemberControllerTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private MemberRepository memberRepository;

    @BeforeEach
    void init() {
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("id 와 certificationCode 를 통해 Status 값을 ACTIVE 로 변경할 수 있다.")
    void change_active_status_by_id_and_certificationCode() throws Exception{
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
        mockMvc.perform(get("/api/members/" + member.getId() + "/verify/aaaa-aaaa-aaaa"))
                .andDo(print())
                .andExpect(status().isFound());

        // then
        Member result = memberRepository.getById(member.getId());
        assertThat(result.getStatus()).isEqualTo(MemberStatus.ACTIVE);
    }
}