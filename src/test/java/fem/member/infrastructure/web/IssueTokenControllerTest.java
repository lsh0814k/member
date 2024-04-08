package fem.member.infrastructure.web;

import fem.member.application.JwtService;
import fem.member.application.jwt.JwtProperties;
import fem.member.application.port.MemberRepository;
import fem.member.domain.Member;
import fem.member.domain.vo.MemberStatus;
import fem.member.domain.vo.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.ZonedDateTime;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ReissueTokenControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private JwtService jwtService;
    @Autowired private JwtProperties jwtProperties;
    @Autowired private MemberRepository memberRepository;

    @Test
    @DisplayName("refresh token 을 이용하여 access token 을 재발급 받을 수 있다.")
    void reissue_accessToken_by_refreshToken() throws Exception {
        // given
        Member member = Member.builder()
                .id(1L)
                .loginId("slee@naver.com")
                .nickname("lee")
                .password("a123456")
                .certificationCode("aaaa-aaaa-aaaa")
                .role(UserRole.USER)
                .status(MemberStatus.ACTIVE)
                .build();
        memberRepository.save(member);

        Instant instant = ZonedDateTime.now()
                .toInstant()
                .plusMillis(jwtProperties.getRefreshTokenExpTime());
        String refreshToken = jwtService.createRefreshToken(jwtProperties.getKey(), instant);
        jwtService.addRefreshToken("slee@naver.com", refreshToken, instant);

        // when
        // then
        mockMvc.perform(post("/api/reissue")
                        .contentType(APPLICATION_JSON)
                        .header("Authorization-refresh", refreshToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().exists("Authorization-refresh"))
                .andExpect(header().exists("Authorization"));
    }

    @Test
    @DisplayName("잘못된 refresh token 을 이용하여 access token 을 재발급 요청을 하면 상태 코드 400을 반환한다.")
    void wrong_refreshToken() throws Exception {
        // given
        Member member = Member.builder()
                .id(1L)
                .loginId("slee@naver.com")
                .nickname("lee")
                .password("a123456")
                .certificationCode("aaaa-aaaa-aaaa")
                .role(UserRole.USER)
                .status(MemberStatus.ACTIVE)
                .build();
        memberRepository.save(member);

        Instant instant = ZonedDateTime.now()
                .toInstant()
                .minusMillis(5000);
        String refreshToken = jwtService.createRefreshToken(jwtProperties.getKey(), instant);
        jwtService.addRefreshToken("slee@naver.com", refreshToken, instant);

        // when
        // then
        mockMvc.perform(post("/api/reissue")
                        .contentType(APPLICATION_JSON)
                        .header("Authorization-refresh", refreshToken + "aa"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("기간이 만료된 refresh token 을 이용하여 access token 을 재발급 요청을 하면 상태 코드 401을 반환한다.")
    void expired_refreshToken() throws Exception {
        // given
        Member member = Member.builder()
                .id(1L)
                .loginId("slee@naver.com")
                .nickname("lee")
                .password("a123456")
                .certificationCode("aaaa-aaaa-aaaa")
                .role(UserRole.USER)
                .status(MemberStatus.ACTIVE)
                .build();
        memberRepository.save(member);

        Instant instant = ZonedDateTime.now()
                .toInstant()
                .minusMillis(5000);
        String refreshToken = jwtService.createRefreshToken(jwtProperties.getKey(), instant);
        jwtService.addRefreshToken("slee@naver.com", refreshToken, instant);

        // when
        // then
        mockMvc.perform(post("/api/reissue")
                        .contentType(APPLICATION_JSON)
                        .header("Authorization-refresh", refreshToken))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}