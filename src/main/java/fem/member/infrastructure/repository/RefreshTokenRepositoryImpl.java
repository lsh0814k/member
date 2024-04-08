package fem.member.infrastructure.repository;

import fem.member.application.port.RefreshTokenRepository;
import fem.member.domain.RefreshToken;
import fem.member.domain.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {
    private final RefreshTokenJpaRepository repository;

    @Override
    public RefreshToken getByToken(String token) {
        return repository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("RefreshToken", token));
    }

    @Override
    public RefreshToken save(RefreshToken token) {
        return repository.save(token);
    }

    @Override
    public void deleteByLoginId(String loginId) {
        repository.deleteByLoginId(loginId);
    }

    @Override
    public List<RefreshToken> findAllByLoginId(String loginId) {
        return repository.findAllByLoginId(loginId);
    }

    @Override
    public void deleteByToken(String refreshToken) {
        repository.deleteByToken(refreshToken);
    }
}
