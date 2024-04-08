package fem.mock;

import fem.member.application.port.RefreshTokenRepository;
import fem.member.domain.RefreshToken;
import fem.member.domain.exception.ResourceNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FakeRefreshTokenRepository implements RefreshTokenRepository {
    private final List<RefreshToken> datas = new ArrayList<>();
    private Long generatedValue = 1L;

    @Override
    public RefreshToken getByToken(String token) {
        return datas.stream()
                .filter(item -> Objects.equals(item.getToken(), token))
                .findAny()
                .orElseThrow(() -> new ResourceNotFoundException("RefreshToken", token));
    }

    @Override
    public RefreshToken save(RefreshToken token) {
        if (token.getId() == null) {
            RefreshToken newToken = RefreshToken.builder()
                    .id(generatedValue++)
                    .loginId(token.getLoginId())
                    .token(token.getToken())
                    .expiration(token.getExpiration())
                    .build();
            datas.add(newToken);

            return newToken;
        } else {
            datas.removeIf(item -> Objects.equals(item.getId(), token.getId()));
            datas.add(token);

            return token;
        }
    }

    @Override
    public void deleteByLoginId(String loginId) {
        List<RefreshToken> removeList = datas.stream().filter(item -> Objects.equals(item.getLoginId(), loginId))
                .toList();
        datas.removeAll(removeList);
    }

    @Override
    public List<RefreshToken> findAllByLoginId(String loginId) {
        return datas.stream().filter(item -> Objects.equals(item.getLoginId(), loginId))
                .toList();
    }

    @Override
    public void deleteByToken(String refreshToken) {
        datas.removeIf(item -> Objects.equals(item.getToken(), refreshToken));
    }
}
