package fem.member.application;

import fem.member.application.jwt.UserToken;
import fem.member.application.port.MemberRepository;
import fem.member.domain.Member;
import fem.member.infrastructure.web.request.MemberInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserTokenService implements UserDetailsService {
    private final MemberRepository memberRepository;
    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        log.info("UserTokenService.loadUserByUsername {}", loginId);
        Member member = memberRepository.getByLoginIdAndStatus(loginId);
        return new UserToken(MemberInfo.from(member));
    }
}
