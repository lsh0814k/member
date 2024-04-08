package fem.member.infrastructure.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import fem.member.infrastructure.web.request.LoginRequest;
import fem.member.application.jwt.JwtProperties;
import fem.member.application.JwtService;
import fem.member.infrastructure.web.response.LoginResponse;
import fem.member.application.jwt.UserToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.time.ZonedDateTime;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JwtProperties jwtProperties;
    private final JwtService jwtService;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.info("[JwtAuthenticationFilter.attemptAuthentication] 로그인 시도");
        Authentication authenticate = null;
        try {
            // username, password 를 통해 LoginRequest 생성
            ObjectMapper om = new ObjectMapper();
            LoginRequest loginRequest = om.readValue(request.getInputStream(), LoginRequest.class);
            log.info("loginId: {} ", loginRequest.getUsername());
            log.info("[JwtAuthenticationFilter.attemptAuthentication] 정상적인 로그인 시도 여부를 검증한다.");

            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());
            authenticate = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
            log.info("[JwtAuthenticationFilter.attemptAuthentication] 로그인 성공");

        } catch (IOException e) {
            log.info("[JwtAuthenticationFilter.attemptAuthentication] 로그인 시도 중 오류가 발생 했습니다. ", e);
        }

        // 반환된 authenticate 는 세션에 저장이 된다.
        return authenticate;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        UserToken userToken = (UserToken) authResult.getPrincipal();
        log.info("[인증 성공] loginId: {}", userToken.getUsername());
        ZonedDateTime now = ZonedDateTime.now();

        String accessToken = jwtService.createAccessToken(userToken.getMemberInfo(),
                jwtProperties.getKey(),
                now.toInstant().plusMillis(jwtProperties.getAccessTokenExpTime()));

        String refreshToken = jwtService.createRefreshToken(jwtProperties.getKey(),
                now.toInstant().plusMillis(jwtProperties.getRefreshTokenExpTime()));

        jwtService.addRefreshToken(userToken.getUsername(), refreshToken, now.toInstant());

        log.info("token 발급");
        response.addHeader(jwtProperties.getHeaderPrefix(), jwtProperties.getTokenPrefix() + accessToken);
        response.addHeader(jwtProperties.getRefreshHeaderPrefix(), refreshToken);
        addResponseMessage(response, "로그인 성공");
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        addResponseFailMessage(response, failed.getMessage());
    }

    private void addResponseMessage(HttpServletResponse response, String message) throws IOException{
        response.setStatus(SC_OK);
        response.setContentType(APPLICATION_JSON_VALUE);

        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().println(
                        objectMapper.writeValueAsString(
                                LoginResponse.builder()
                                        .code(1)
                                        .success(true)
                                        .message(message)
                                        .build()
                        )
                );
    }

    private void addResponseFailMessage(HttpServletResponse response, String message) throws IOException {
        response.setStatus(SC_BAD_REQUEST);
        response.setContentType(APPLICATION_JSON_VALUE);
        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().println(
                objectMapper.writeValueAsString(
                        LoginResponse.builder()
                                .code(-1)
                                .success(false)
                                .message(message)
                                .build()
                )
        );
    }
}
