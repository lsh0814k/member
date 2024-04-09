package fem.member.infrastructure.repository;

import fem.member.domain.RefreshToken;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenJpaRepository extends CrudRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    void deleteByLoginId(String loginId);

    List<RefreshToken> findAllByLoginId(String loginId);

    void deleteByToken(String refreshToken);
}
