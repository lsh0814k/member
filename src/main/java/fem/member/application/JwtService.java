package fem.member.application;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import fem.member.application.jwt.JwtErrorMessage;
import fem.member.application.port.MemberRepository;
import fem.member.application.port.RefreshTokenRepository;
import fem.member.domain.Member;
import fem.member.domain.RefreshToken;
import fem.member.infrastructure.web.request.MemberInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class JwtService {
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public String createAccessToken(MemberInfo memberInfo, String key, Instant expire) {
        return JWT.create()
                .withSubject("jwtToken")
                .withExpiresAt(Date.from(expire))
                .withClaim("loginId", memberInfo.getLoginId())
                .withClaim("role", memberInfo.getRole().toString())
                .sign(getKeyAlgorithm(key));
    }

    public String createRefreshToken(String key, Instant expire) {
        return JWT.create()
                .withSubject("refreshToken")
                .withExpiresAt(Date.from(expire))
                .sign(getKeyAlgorithm(key));
    }
    public boolean isValidHeader(String token, String prefix) {
        return token != null && token.startsWith(prefix);
    }

    public RefreshToken addRefreshToken(String loginId, String token, Instant expire) {
        RefreshToken refreshToken = RefreshToken.create(loginId, token, LocalDateTime.ofInstant(expire, ZonedDateTime.now().getZone()));
        refreshTokenRepository.deleteByLoginId(loginId);
        refreshTokenRepository.save(refreshToken);

        return refreshToken;
    }

    @Transactional(readOnly = true)
    public MemberInfo getMemberInfoByRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.getByToken(token);
        return MemberInfo.from(memberRepository.getByLoginIdAndStatus(refreshToken.getLoginId()));
    }

    @Transactional(readOnly = true)
    public MemberInfo getMemberInfoByUsername(String token, String key) {
        String username = getUsername(token, key);
        Member member = memberRepository.getByLoginIdAndStatus(username);
        return MemberInfo.from(member);
    }

    private String getUsername(String token, String key) {
        return JWT.require(getKeyAlgorithm(key))
                .build()
                .verify(token)
                .getClaim("loginId")
                .asString();
    }
    public boolean isNotExpiredRefreshToken(String token, String key) {
        try {
            JWT.require(getKeyAlgorithm(key))
                    .build()
                    .verify(token);
            return true;
        } catch (TokenExpiredException e) {
            throw new AuthorizationServiceException(JwtErrorMessage.JWT_REFRESH_IS_EXPIRED.getMessage());
        } catch (Exception e) {
            throw new AuthorizationServiceException(JwtErrorMessage.JWT_REFRESH_IS_NOT_VALID.getMessage());
        }
    }

    public boolean isNotExpiredAccessToken(String accessToken, String key) {
        try {
            JWT.require(getKeyAlgorithm(key))
                    .build()
                    .verify(accessToken);
            return true;
        } catch (TokenExpiredException e) {
            throw new AuthorizationServiceException(JwtErrorMessage.JWT_ACCESS_IS_EXPIRED.getMessage());
        } catch (Exception e) {
            throw new AuthorizationServiceException(JwtErrorMessage.JWT_ACCESS_IS_NOT_VALID.getMessage());
        }
    }

    public Algorithm getKeyAlgorithm(String key) {
        return Algorithm.HMAC512(key);
    }
}
