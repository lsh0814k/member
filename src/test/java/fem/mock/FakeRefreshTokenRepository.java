package fem.mock;

import fem.member.application.port.RefreshTokenRepository;
import fem.member.domain.RefreshToken;
import fem.member.domain.exception.ResourceNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FakeRefreshTokenRepository implements RefreshTokenRepository {
    private final List<RefreshToken> datas = new ArrayList<>();

    @Override
    public RefreshToken getByToken(String token) {
        return datas.stream()
                .filter(item -> Objects.equals(item.getToken(), token))
                .findAny()
                .orElseThrow(() -> new ResourceNotFoundException("RefreshToken", token));
    }

    @Override
    public RefreshToken save(RefreshToken token) {
        datas.removeIf(item -> Objects.equals(item.getToken(), token.getToken()));
        datas.add(token);

        return token;
    }

    @Override
    public void deleteByToken(String refreshToken) {
        datas.removeIf(item -> Objects.equals(item.getToken(), refreshToken));
    }
}
