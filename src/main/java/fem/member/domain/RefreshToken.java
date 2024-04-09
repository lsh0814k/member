package fem.member.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;


import static lombok.AccessLevel.PROTECTED;

@Getter
@Builder
@AllArgsConstructor
@RedisHash(value = "refreshToken")
@NoArgsConstructor(access = PROTECTED)
@EqualsAndHashCode(of = "token")
public class RefreshToken {
    @Id
    private String token;
    private String loginId;
    @TimeToLive
    private Long expiration;

    public static RefreshToken create(String loginId, String token, long expiration) {
        return RefreshToken.builder()
                .loginId(loginId)
                .token(token)
                .expiration(expiration)
                .build();
    }
}
