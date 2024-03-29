package fem.member.infrastructure.web.validator;


import fem.member.application.port.MemberRepository;
import fem.member.infrastructure.repository.MemberJpaRepository;
import fem.member.infrastructure.web.request.SignupRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class SignupValidator implements Validator {
    private final MemberRepository memberRepository;
    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(SignupRequest.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        SignupRequest signupRequest = (SignupRequest) target;
        if (!signupRequest.getPassword().equals(signupRequest.getPasswordConf())) {
            errors.rejectValue("passwordConf", "mismatch.password", "비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }

        if (memberRepository.findByLoginId(signupRequest.getLoginId()).isPresent()) {
            errors.rejectValue("loginId", "invalid.loginId", "이미 사용중인 로그인 아이디 입니다.");
        }
    }
}
