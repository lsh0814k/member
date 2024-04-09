package fem.member.infrastructure.web;

import fem.member.application.JwtService;
import fem.member.application.jwt.JwtProperties;
import fem.member.application.port.MemberRepository;
import fem.member.domain.Member;
import fem.member.domain.vo.MemberStatus;
import fem.member.domain.vo.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.ZonedDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class LogoutTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private JwtProperties jwtProperties;
    @Autowired private JwtService jwtService;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private MemberRepository memberRepository;

    @BeforeEach
    void init() {
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("유효한 refresh token 으로 로그아웃을 할 수 있다.")
    void logout_for_valid_refreshToken() throws Exception {
        // given
        Member member = Member.builder()
                .loginId("slee@naver.com")
                .nickname("lee")
                .password(passwordEncoder.encode("a123456"))
                .certificationCode("aaaa-aaaa-aaaa")
                .role(UserRole.USER)
                .status(MemberStatus.ACTIVE)
                .build();
        memberRepository.save(member);
        Instant instant = ZonedDateTime.now()
                .toInstant()
                .plusMillis(jwtProperties.getRefreshTokenExpTime());

        String refreshToken = jwtService.createRefreshToken(jwtProperties.getKey(), instant);

        // when
        // then
        mockMvc.perform(post("/logout")
                .header(jwtProperties.getRefreshHeaderPrefix(), refreshToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().doesNotExist(jwtProperties.getHeaderPrefix()))
                .andExpect(header().doesNotExist(jwtProperties.getRefreshHeaderPrefix()));
    }

    @Test
    @DisplayName("잘못된 refresh token 으로 로그아웃을 할 경우 상태 코드 400을 반환한다.")
    void logout_for_wrong_refreshToken() throws Exception {
        // given
        Member member = Member.builder()
                .loginId("slee@naver.com")
                .nickname("lee")
                .password(passwordEncoder.encode("a123456"))
                .certificationCode("aaaa-aaaa-aaaa")
                .role(UserRole.USER)
                .status(MemberStatus.ACTIVE)
                .build();
        memberRepository.save(member);
        Instant instant = ZonedDateTime.now()
                .toInstant()
                .minusMillis(jwtProperties.getRefreshTokenExpTime());

        String refreshToken = jwtService.createRefreshToken(jwtProperties.getKey(), instant)
                + "aaa";

        // when
        // then
        mockMvc.perform(post("/logout")
                        .header(jwtProperties.getRefreshHeaderPrefix(), refreshToken))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(header().doesNotExist(jwtProperties.getHeaderPrefix()))
                .andExpect(header().doesNotExist(jwtProperties.getRefreshHeaderPrefix()));
    }

    @Test
    @DisplayName("만료된 refresh token 으로 로그아웃을 할 경우 상태 코드 401을 반환한다.")
    void logout_for_expired_refreshToken() throws Exception {
        // given
        Member member = Member.builder()
                .loginId("slee@naver.com")
                .nickname("lee")
                .password(passwordEncoder.encode("a123456"))
                .certificationCode("aaaa-aaaa-aaaa")
                .role(UserRole.USER)
                .status(MemberStatus.ACTIVE)
                .build();
        memberRepository.save(member);
        Instant instant = ZonedDateTime.now()
                .toInstant()
                .minusMillis(jwtProperties.getRefreshTokenExpTime());

        String refreshToken = jwtService.createRefreshToken(jwtProperties.getKey(), instant);

        // when
        // then
        mockMvc.perform(post("/logout")
                        .header(jwtProperties.getRefreshHeaderPrefix(), refreshToken))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(header().doesNotExist(jwtProperties.getHeaderPrefix()))
                .andExpect(header().doesNotExist(jwtProperties.getRefreshHeaderPrefix()));
    }
}
