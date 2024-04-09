package fem.member.application.port;

import fem.member.domain.Member;
import fem.member.domain.vo.MemberStatus;

import java.util.Optional;

public interface MemberRepository {

    Member save(Member member);

    Optional<Member> findByLoginId(String loginId);

    Member getById(Long id);

    Member getByIdAndStatus(Long id, MemberStatus status);

    Member getByLoginIdAndStatus(String loginId);

    void deleteAll();
}
