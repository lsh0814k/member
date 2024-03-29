package fem.member.infrastructure.web;

import fem.member.application.SignupService;
import fem.member.domain.Member;
import fem.member.infrastructure.web.request.SignupRequest;
import fem.member.infrastructure.web.response.MemberResponse;
import fem.member.infrastructure.web.validator.SignupValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class SignupController {
    private final SignupService signupService;
    private final SignupValidator signupValidator;

    @InitBinder("signupRequest")
    void passwordBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(signupValidator);
    }

    @PostMapping
    public ResponseEntity<MemberResponse> signup(@RequestBody @Valid SignupRequest signupRequest) {
        Member member = signupService.signup(signupRequest.toModel());
        return ResponseEntity.status(CREATED)
                .body(MemberResponse.from(member));
    }
}
