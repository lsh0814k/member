package fem.member.application.port;

import fem.member.domain.Member;

import java.util.Optional;

public interface MemberRepository {

    Member save(Member member);

    Optional<Member> findByLoginId(String loginId);

    Member getById(Long id);
}
