package fem.member.infrastructure.repository;

import fem.member.application.port.MemberRepository;
import fem.member.domain.Member;
import fem.member.domain.exception.ResourceNotFoundException;
import fem.member.domain.vo.MemberStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static fem.member.domain.vo.MemberStatus.*;

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

    @Override
    public Member getByIdAndStatus(Long id, MemberStatus status) {
        return memberJpaRepository.findByIdAndStatus(id, status)
                .orElseThrow(() -> new ResourceNotFoundException("Member", id));
    }

    @Override
    public Member getByLoginIdAndStatus(String loginId) {
        return memberJpaRepository.findByLoginIdAndStatus(loginId, ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Member", loginId));
    }

    @Override
    public void deleteAll() {
        memberJpaRepository.deleteAll();
    }
}
