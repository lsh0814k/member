package fem.member.application;

import fem.member.application.port.MemberRepository;
import fem.member.domain.Member;
import fem.member.domain.vo.MemberStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public void verifyEmail(Long id, String certificationCode) {
        Member member = memberRepository.getByIdAndStatus(id, MemberStatus.PENDING);
        member.certificate(certificationCode);
    }
}
