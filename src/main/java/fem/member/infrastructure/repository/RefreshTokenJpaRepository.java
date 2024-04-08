package fem.member.infrastructure.repository;

import fem.member.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshToken, Long> {
    boolean existsByToken(String token);

    void deleteByToken(String token);

    Optional<RefreshToken> findByToken(String token);
}
