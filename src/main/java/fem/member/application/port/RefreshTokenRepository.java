package fem.member.application.port;

import fem.member.domain.RefreshToken;

import java.util.List;

public interface RefreshTokenRepository {
    RefreshToken getByToken(String token);
    RefreshToken save(RefreshToken token);
    void deleteByLoginId(String loginId);

    List<RefreshToken> findAllByLoginId(String loginId);
}
