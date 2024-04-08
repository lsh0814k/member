package fem.member.infrastructure.repository;

import fem.member.domain.Member;
import fem.member.domain.vo.MemberStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberJpaRepository extends JpaRepository<Member,Long> {
    Optional<Member> findByLoginId(String loginId);

    Optional<Member> findByIdAndStatus(Long id, MemberStatus status);

    Optional<Member> findByLoginIdAndStatus(String loginId, MemberStatus memberStatus);
}
