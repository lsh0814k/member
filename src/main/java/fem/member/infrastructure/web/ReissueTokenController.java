package fem.member.infrastructure.web;

import com.auth0.jwt.exceptions.TokenExpiredException;
import fem.member.application.jwt.JwtErrorMessage;
import fem.member.application.jwt.JwtProperties;
import fem.member.application.JwtService;
import fem.member.infrastructure.web.request.MemberInfo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reissue")
public class ReissueTokenController {
    private final JwtProperties jwtProperties;
    private final JwtService jwtService;

    @PostMapping
    public ResponseEntity<String> issueToken(HttpServletResponse response, HttpServletRequest request) {
        try {
            String refreshToken = request.getHeader(jwtProperties.getRefreshHeaderPrefix());

            jwtService.isNotExpiredRefreshToken(refreshToken, jwtProperties.getKey());
            MemberInfo memberInfo = jwtService.getMemberInfoByRefreshToken(refreshToken);
            log.info("access token 재발급 {}", memberInfo.getLoginId());
            ZonedDateTime now = ZonedDateTime.now();

            String accessToken = jwtService.createAccessToken(memberInfo,
                    jwtProperties.getKey(),
                    now.toInstant().plusMillis(jwtProperties.getAccessTokenExpTime()));
            String newRefreshToken = jwtService.createRefreshToken(jwtProperties.getKey(),
                    now.toInstant().plusMillis(jwtProperties.getRefreshTokenExpTime()));

            jwtService.addRefreshToken(memberInfo.getLoginId(), newRefreshToken, now.toInstant().plusMillis(jwtProperties.getRefreshTokenExpTime()));
            response.addHeader(jwtProperties.getRefreshHeaderPrefix(), newRefreshToken);
            response.addHeader(jwtProperties.getHeaderPrefix(), jwtProperties.getTokenPrefix() + accessToken);
        } catch (TokenExpiredException e) { // 인증이 만료된 경우
            log.info("인증이 만료되었습니다. ", e);
            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
                            .body(JwtErrorMessage.JWT_ACCESS_IS_EXPIRED.getMessage());
        } catch (Exception e) { // 잘못된 인증코드가 요청된 경우
            log.info("잘못된 인증 코드 입니다. ", e);
            return ResponseEntity.status(HttpServletResponse.SC_BAD_REQUEST)
                    .body(JwtErrorMessage.JWT_ACCESS_IS_NOT_VALID.getMessage());
        }

        return ResponseEntity.ok()
                .body("인증코드가 발급되었습니다.");
    }
}
