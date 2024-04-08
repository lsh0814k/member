package fem.member.application;

import com.auth0.jwt.exceptions.JWTDecodeException;
import fem.member.application.jwt.JwtErrorMessage;
import fem.member.application.jwt.JwtProperties;
import fem.member.application.port.MemberRepository;
import fem.member.application.port.RefreshTokenRepository;
import fem.member.domain.Member;
import fem.member.domain.RefreshToken;
import fem.member.domain.exception.ResourceNotFoundException;
import fem.member.domain.vo.MemberStatus;
import fem.member.domain.vo.UserRole;
import fem.member.infrastructure.web.request.MemberInfo;
import fem.mock.FakeMemberRepository;
import fem.mock.FakeRefreshTokenRepository;
import fem.mock.StubJwtProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AuthorizationServiceException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {
    private MemberRepository memberRepository;
    private RefreshTokenRepository refreshTokenRepository;
    private JwtService jwtService;

    @BeforeEach
    void init() {
        this.memberRepository = new FakeMemberRepository();
        this.refreshTokenRepository = new FakeRefreshTokenRepository();
        this.jwtService = new JwtService(memberRepository, refreshTokenRepository);
    }

    @Test
    @DisplayName("회원 정보를 통해 access token 을 생성할 수 있다")
    void create_accessToken_by_memberInfo() {
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
        MemberInfo memberInfo = MemberInfo.from(member);
        JwtProperties properties = StubJwtProperties.create();
        Instant instant = ZonedDateTime.now()
                .toInstant()
                .plusMillis(properties.getAccessTokenExpTime());

        // when
        String result = jwtService.createAccessToken(memberInfo, properties.getKey(), instant);

        // then
        assertThat(result).isNotBlank();
    }

    @Test
    @DisplayName("refresh token 을 생성할 수 있다.")
    void create_refreshToken() {
        // given
        JwtProperties properties = StubJwtProperties.create();
        Instant instant = ZonedDateTime.now()
                .toInstant()
                .plusMillis(properties.getRefreshTokenExpTime());

        // when
        String result = jwtService.createRefreshToken(properties.getKey(), instant);

        // then
        assertThat(result).isNotBlank();
    }

    @Test
    @DisplayName("access token 의 시작 문자가 특정 문자로 시작하는지 확인할 수 있다.")
    void start_with_Bearer_accessToken() {
        // given
        String token = "Bearer aawwaaa";
        String prefix = "Bearer ";
        // when
        boolean result = jwtService.isValidHeader(token, prefix);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("refresh token 으로 회원 정보를 조회할 수 있다.")
    void memberInfo_by_refreshToken() {
        // given
        Member member = Member.builder()
                .loginId("slee@naver.com")
                .nickname("lee")
                .password("a123456")
                .certificationCode("aaaa-aaaa-aaaa")
                .role(UserRole.USER)
                .status(MemberStatus.ACTIVE)
                .build();
        memberRepository.save(member);
        String token = "1q2w3e4r";
        refreshTokenRepository.save(RefreshToken.create("slee@naver.com", token, LocalDateTime.now()));

        // when
        MemberInfo result = jwtService.getMemberInfoByRefreshToken(token);

        // then
        assertThat(result.getLoginId()).isEqualTo("slee@naver.com");
    }

    @Test
    @DisplayName("잘못된 refresh token 으로 조회할 경우 ResourceNotFoundException 이 발생한다")
    void memberInfo_by_non_exists_refreshToken() {
        // given
        String token = "1q2w3e4r";

        // when
        // then
        assertThatThrownBy(() -> jwtService.getMemberInfoByRefreshToken(token))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("access token 을 이용해 memberInfo 를 조회할 수 있다.")
    void memberInfo_by_accessToken() {
        // given
        Member member = Member.builder()
                .loginId("slee@naver.com")
                .nickname("lee")
                .password("a123456")
                .certificationCode("aaaa-aaaa-aaaa")
                .role(UserRole.USER)
                .status(MemberStatus.ACTIVE)
                .build();
        memberRepository.save(member);
        MemberInfo memberInfo = MemberInfo.from(member);
        JwtProperties properties = StubJwtProperties.create();
        Instant instant = ZonedDateTime.now()
                .toInstant()
                .plusMillis(properties.getAccessTokenExpTime());
        String accessToken = jwtService.createAccessToken(memberInfo, properties.getKey(), instant);

        // when
        MemberInfo result = jwtService.getMemberInfoByUsername(accessToken, properties.getKey());

        // then
        assertThat(result.getLoginId()).isEqualTo("slee@naver.com");
    }

    @Test
    @DisplayName("잘못된 access token 으로 조회할 경우 JWTDecodeException 이 발생한다")
    void memberInfo_by_non_exists_accessToken() {
        // given
        String token = "1q2w3e4r";
        JwtProperties properties = StubJwtProperties.create();

        // when
        // then
        assertThatThrownBy(() -> jwtService.getMemberInfoByUsername(token, properties.getKey()))
                .isInstanceOf(JWTDecodeException.class);
    }

    @Test
    @DisplayName("access token 이 만료 되지 않았으면 예외가 발생하지 않는다.")
    void accessToken_non_exception() {
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
        MemberInfo memberInfo = MemberInfo.from(member);
        JwtProperties properties = StubJwtProperties.create();
        Instant instant = ZonedDateTime.now()
                .toInstant()
                .plusMillis(properties.getAccessTokenExpTime());
        String accessToken = jwtService.createAccessToken(memberInfo, properties.getKey(), instant);

        // when
        boolean result = jwtService.isNotExpiredAccessToken(accessToken, properties.getKey());

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("access token 이 만료 되었으면 AuthorizationServiceException 이 발생한다.")
    void accessToken_expired() {
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
        MemberInfo memberInfo = MemberInfo.from(member);
        JwtProperties properties = StubJwtProperties.create();
        Instant instant = ZonedDateTime.now()
                .toInstant()
                .minusMillis(1000);
        String accessToken = jwtService.createAccessToken(memberInfo, properties.getKey(), instant);

        // when
        // then
        assertThatThrownBy(() -> jwtService.isNotExpiredAccessToken(accessToken, properties.getKey()))
                .isInstanceOf(AuthorizationServiceException.class)
                .hasMessage(JwtErrorMessage.JWT_ACCESS_IS_EXPIRED.getMessage());
    }

    @Test
    @DisplayName("access token 이 만료 되었으면 AuthorizationServiceException 이 발생한다.")
    void accessToken_unexpected() {
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
        MemberInfo memberInfo = MemberInfo.from(member);
        JwtProperties properties = StubJwtProperties.create();
        Instant instant = ZonedDateTime.now()
                .toInstant()
                .minusMillis(1000);
        String accessToken = jwtService.createAccessToken(memberInfo, properties.getKey(), instant)
                + "aaa";

        // when
        // then
        assertThatThrownBy(() -> jwtService.isNotExpiredAccessToken(accessToken, properties.getKey()))
                .isInstanceOf(AuthorizationServiceException.class)
                .hasMessage(JwtErrorMessage.JWT_ACCESS_IS_NOT_VALID.getMessage());

    }

    @Test
    @DisplayName("refresh token 이 만료 되지 않았으면 예외가 발생하지 않는다.")
    void refreshToken_non_exception() {
        // given
        JwtProperties properties = StubJwtProperties.create();
        Instant instant = ZonedDateTime.now()
                .toInstant()
                .plusMillis(properties.getRefreshTokenExpTime());
        String token = jwtService.createRefreshToken(properties.getKey(), instant);

        // when
        boolean result = jwtService.isNotExpiredRefreshToken(token, properties.getKey());

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("refresh token 이 만료 되었으면 AuthorizationServiceException 이 발생한다.")
    void refreshToken_expired() {
        // given
        JwtProperties properties = StubJwtProperties.create();
        Instant instant = ZonedDateTime.now()
                .toInstant()
                .minusMillis(2000);
        String token = jwtService.createRefreshToken(properties.getKey(), instant);

        // when
        // then
        assertThatThrownBy(() -> jwtService.isNotExpiredRefreshToken(token, properties.getKey()))
                .isInstanceOf(AuthorizationServiceException.class)
                .hasMessage(JwtErrorMessage.JWT_REFRESH_IS_EXPIRED.getMessage());
    }

    @Test
    @DisplayName("refresh token 이 만료 되었으면 AuthorizationServiceException 이 발생한다.")
    void refreshToken_unexpected() {
        // given
        JwtProperties properties = StubJwtProperties.create();
        Instant instant = ZonedDateTime.now()
                .toInstant()
                .minusMillis(2000);
        String token = jwtService.createRefreshToken(properties.getKey(), instant)
                + "aaa";

        // when
        // then
        assertThatThrownBy(() -> jwtService.isNotExpiredAccessToken(token, properties.getKey()))
                .isInstanceOf(AuthorizationServiceException.class)
                .hasMessage(JwtErrorMessage.JWT_ACCESS_IS_NOT_VALID.getMessage());

    }
}