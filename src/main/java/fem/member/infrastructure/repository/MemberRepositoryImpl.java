package fem.member.infrastructure.repository;

import fem.member.application.port.MemberRepository;
import fem.member.domain.Member;
import fem.member.domain.exception.ResourceExistException;
import fem.member.domain.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {
    private final MemberJpaRepository memberJpaRepository;

    @Override
    public Member save(Member member) {
        return memberJpaRepository.save(member);
    }

    @Override
    public Optional<Member> findByLoginId(String loginId) {
        return memberJpaRepository.findByLoginId(loginId);
    }

    @Override
    public Member getById(Long id) {
        return memberJpaRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Member", id));
    }


}
