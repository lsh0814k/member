package fem.member.infrastructure.filter;

import com.auth0.jwt.exceptions.TokenExpiredException;
import fem.member.infrastructure.web.request.MemberInfo;
import fem.member.application.jwt.JwtErrorMessage;
import fem.member.application.jwt.JwtProperties;
import fem.member.application.JwtService;
import fem.member.application.jwt.UserToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final JwtProperties jwtProperties;
    private final JwtService jwtService;

    public JwtAuthorizationFilter(JwtProperties jwtProperties, JwtService jwtService) {
        this.jwtProperties = jwtProperties;
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 토큰이 없는 경우
        if (!jwtService.isValidHeader(request.getHeader(jwtProperties.getHeaderPrefix()), jwtProperties.getTokenPrefix())) {
            filterChain.doFilter(request, response);
            return;
        }

        log.info("[JwtAuthorizationFilter.doFilterInternal] check JWT");
        try {
            String accessToken = request.getHeader(jwtProperties.getHeaderPrefix())
                    .replace(jwtProperties.getTokenPrefix(), "");

            // 토큰이 유효한지 확인
            jwtService.isNotExpiredAccessToken(accessToken, jwtProperties.getKey());

            MemberInfo memberInfo = jwtService.getMemberInfoByUsername(accessToken, jwtProperties.getKey());
            UserToken userToken = new UserToken(memberInfo);
            Authentication authentication = new UsernamePasswordAuthenticationToken(userToken, null, userToken.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);
        } catch (TokenExpiredException e) { // 인증이 만료된 경우
            log.info("인증이 만료되었습니다. ", e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            request.setAttribute("errorMsg", JwtErrorMessage.JWT_ACCESS_IS_NOT_VALID);
        } catch (Exception e) { // 잘못된 인증코드가 요청된 경우
            log.info("잘못된 인증 코드 입니다. ", e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            request.setAttribute("errorMsg", JwtErrorMessage.JWT_IS_NOT_VALID);
        }
    }
}
