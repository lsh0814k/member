package fem.member.application.port;

import fem.member.domain.RefreshToken;

public interface RefreshTokenRepository {

    RefreshToken getByToken(String token);

    boolean existsByToken(String token);

    void deleteByToken(String token);

    RefreshToken save(RefreshToken token);
}
