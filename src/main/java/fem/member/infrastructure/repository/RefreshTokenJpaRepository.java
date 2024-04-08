package fem.member.infrastructure.repository;

import fem.member.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    void deleteByLoginId(String loginId);

    List<RefreshToken> findAllByLoginId(String loginId);
}
