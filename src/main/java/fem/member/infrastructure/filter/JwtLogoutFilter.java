package fem.member.infrastructure.filter;

import com.auth0.jwt.exceptions.TokenExpiredException;
import fem.member.application.JwtService;
import fem.member.application.jwt.JwtErrorMessage;
import fem.member.application.jwt.JwtProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtLogoutFilter extends GenericFilterBean {
    private final JwtProperties jwtProperties;
    private final JwtService jwtService;
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        doFilter((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse, filterChain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!request.getRequestURI().matches("/logout")) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!request.getMethod().equals("POST")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String refreshToken = request.getHeader(jwtProperties.getRefreshHeaderPrefix());
            jwtService.isNotExpiredRefreshToken(refreshToken, jwtProperties.getKey());

            // refresh token 삭제
            jwtService.deleteRefreshToken(refreshToken);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (TokenExpiredException e) { // 인증이 만료된 경우
            log.info("인증이 만료되었습니다. ", e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            request.setAttribute("errorMsg", JwtErrorMessage.JWT_REFRESH_IS_EXPIRED);
        } catch (Exception e) { // 잘못된 인증코드가 요청된 경우
            log.info("잘못된 인증 코드 입니다. ", e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            request.setAttribute("errorMsg", JwtErrorMessage.JWT_REFRESH_IS_NOT_VALID);
        }

        response.setHeader(jwtProperties.getRefreshHeaderPrefix(), null);
        response.setHeader(jwtProperties.getHeaderPrefix(), null);
    }
}
