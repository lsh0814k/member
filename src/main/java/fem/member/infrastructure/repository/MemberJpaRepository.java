package fem.member.infrastructure.repository;

import fem.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberJpaRepository extends JpaRepository<Member,Long> {
    boolean existsByLoginId(String loginId);

    Optional<Member> findByLoginId(String loginId);
}
