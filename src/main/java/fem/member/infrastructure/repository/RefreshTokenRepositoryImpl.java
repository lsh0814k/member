package fem.member.infrastructure.repository;

import fem.member.application.port.RefreshTokenRepository;
import fem.member.domain.RefreshToken;
import fem.member.domain.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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
    public boolean existsByToken(String token) {
        return repository.existsByToken(token);
    }

    @Override
    public void deleteByToken(String token) {
        repository.deleteByToken(token);
    }

    @Override
    public RefreshToken save(RefreshToken token) {
        return repository.save(token);
    }
}
