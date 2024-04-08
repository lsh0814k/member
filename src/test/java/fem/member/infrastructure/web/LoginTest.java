package fem.member.infrastructure.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import fem.member.application.port.MemberRepository;
import fem.member.domain.Member;
import fem.member.domain.vo.MemberStatus;
import fem.member.domain.vo.UserRole;
import fem.member.infrastructure.web.request.LoginRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class LoginTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private MemberRepository memberRepository;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("유효하지 않은 loginId 와 password 로 로그인을 시도하면 가 발생한다.")
    void login_by_wrong_loginId_and_password() throws Exception {
        // given
        Member member = Member.builder()
                .id(1L)
                .loginId("slee@naver.com")
                .nickname("lee")
                .password(passwordEncoder.encode("a123456"))
                .certificationCode("aaaa-aaaa-aaaa")
                .role(UserRole.USER)
                .status(MemberStatus.ACTIVE)
                .build();
        memberRepository.save(member);
        LoginRequest loginRequest = LoginRequest.builder()
                .username("slee@naver.com")
                .password("bbbbbbbb")
                .build();

        // when
        // then
        mockMvc.perform(post("/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(header().doesNotExist("Authorization"))
                .andExpect(header().doesNotExist("Authorization-refresh"))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(-1));
    }

    @Test
    @DisplayName("유효한 loginId 와 password 로 로그인을 할 수 있다.")
    void login_by_valid_loginId_and_password() throws Exception {

        // given
        Member member = Member.builder()
                .id(1L)
                .loginId("slee@naver.com")
                .nickname("lee")
                .password(passwordEncoder.encode("a123456"))
                .certificationCode("aaaa-aaaa-aaaa")
                .role(UserRole.USER)
                .status(MemberStatus.ACTIVE)
                .build();
        memberRepository.save(member);
        LoginRequest loginRequest = LoginRequest.builder()
                .username("slee@naver.com")
                .password("a123456")
                .build();

        // when
        // then
        mockMvc.perform(post("/login")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().exists("Authorization"))
                .andExpect(header().exists("Authorization-refresh"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value(1));
    }


}
