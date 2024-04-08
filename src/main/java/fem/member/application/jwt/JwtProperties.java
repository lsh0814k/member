package fem.member.application.jwt;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Getter
@Component
public class JwtProperties {
    private final String key;
    private final long accessTokenExpTime;
    private final long refreshTokenExpTime;
    private final String tokenPrefix;
    private final String refreshHeaderPrefix;
    private final String headerPrefix;

    public JwtProperties(@Value("${jwt.secret}") String secretKey,
                      @Value("${jwt.token_prefix}") String tokenPrefix,
                      @Value("${jwt.header_prefix}") String headerPrefix,
                      @Value("${jwt.refresh_token_prefix}") String refreshHeaderPrefix,
                      @Value("${jwt.access_expiration_time}") long accessTokenExpTime,
                      @Value("${jwt.refresh_expiration_time}") long refreshTokenExpTime) {
        this.key = secretKey;
        this.accessTokenExpTime = accessTokenExpTime;
        this.refreshTokenExpTime = refreshTokenExpTime;
        this.tokenPrefix = tokenPrefix + " ";
        this.headerPrefix = headerPrefix;
        this.refreshHeaderPrefix = refreshHeaderPrefix;
    }
}
