package fem.member.application;

import fem.member.domain.exception.ResourceExistException;
import fem.member.application.port.UuidHolder;
import fem.member.application.port.MemberRepository;
import fem.member.domain.Member;
import fem.member.domain.MemberCreate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class SignupService {
    private final MemberRepository memberRepository;
    private final UuidHolder uuidHolder;
    private final CertificationService certificationService;
    public Member signup(MemberCreate memberCreate) {
        if (memberRepository.findByLoginId(memberCreate.getLoginId()).isPresent()) {
            throw new ResourceExistException("Member", memberCreate.getLoginId());
        }

        Member member = Member.create(memberCreate, uuidHolder.random());
        member = memberRepository.save(member);

        certificationService.send(member.getLoginId(), member.getId(), member.getCertificationCode());

        return member;
    }
}
