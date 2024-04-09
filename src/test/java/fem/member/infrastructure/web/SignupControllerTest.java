package fem.member.infrastructure.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import fem.member.application.port.MailSender;
import fem.member.application.port.MemberRepository;
import fem.member.domain.Member;
import fem.member.domain.vo.MemberStatus;
import fem.member.domain.vo.UserRole;
import fem.member.infrastructure.web.request.SignupRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SignupControllerTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private MemberRepository memberRepository;
    @MockBean private MailSender mailSender;

    @BeforeEach
    void init() {
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("사용자는 회원 가입을 할 수 있다")
    void signup() throws Exception{
        // given
        // when
        // then
        SignupRequest signupRequest = SignupRequest.builder()
                .loginId("slee@naver.com")
                .nickname("lee")
                .password("123456")
                .passwordConf("123456")
                .build();
        BDDMockito.doNothing().when(mailSender)
                .send(any(String.class), any(String.class), any(String.class));
        mockMvc.perform(post("/api/members")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.loginId").value("slee@naver.com"))
                .andExpect(jsonPath("$.nickname").value("lee"))
                .andExpect(jsonPath("$.status").value(MemberStatus.PENDING.toString()))
                .andExpect(jsonPath("$.role").value(UserRole.USER.toString()));
    }

    @Test
    @DisplayName("회원가입할 때 password 와 passwordConf 는 동일한 값이어야 한다.")
    void signup_password_equal_passwordConf() throws Exception{
        // given
        SignupRequest signupRequest = SignupRequest.builder()
                .loginId("slee@naver.com")
                .nickname("lee")
                .passwordConf("123456")
                .password("234567")
                .build();

        // expected
        mockMvc.perform(post("/api/members")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.errors.[0].field").value("passwordConf"));
    }

    @Test
    @DisplayName("회원가입할 때 loginId, password, passwordConf, nickname 은 필수 값이다")
    void signup_requiredValue() throws Exception{
        // given
        SignupRequest signupRequest = SignupRequest.builder()
                .loginId("")
                .nickname("")
                .passwordConf("")
                .password("")
                .build();
        String expectByFieldName = "$.errors[?(@.field == '%s')]";

        // expected
        mockMvc.perform(post("/api/members")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath(expectByFieldName, "loginId").exists())
                .andExpect(jsonPath(expectByFieldName, "nickname").exists())
                .andExpect(jsonPath(expectByFieldName, "password").exists())
                .andExpect(jsonPath(expectByFieldName, "passwordConf").exists());
    }

    @Test
    @DisplayName("동일한 로그인 아이디가 있으면 예외가 발생된다.")
    void same_loginId_exception() throws Exception{
        // given
        Member member = Member.builder()
                .loginId("slee@naver.com")
                .nickname("lee")
                .password("a123456")
                .certificationCode("aaaa-aaaa-aaaa")
                .role(UserRole.USER)
                .status(MemberStatus.PENDING)
                .build();
        memberRepository.save(member);

        // expected
        SignupRequest signupRequest = SignupRequest.builder()
                .loginId("slee@naver.com")
                .nickname("lee")
                .password("123456")
                .passwordConf("123456")
                .build();

        mockMvc.perform(post("/api/members")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.errors.[0].field").value("loginId"));


    }
}