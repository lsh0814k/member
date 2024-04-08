package fem.member.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PROTECTED;

@Getter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
@EqualsAndHashCode(of = "id")
public class RefreshToken {
    @Id @GeneratedValue
    private Long id;
    private String loginId;
    private String token;
    private LocalDateTime expiration;

    public static RefreshToken create(String loginId, String token, LocalDateTime expiration) {
        return RefreshToken.builder()
                .loginId(loginId)
                .token(token)
                .expiration(expiration)
                .build();
    }
}
