package fem.member.infrastructure.web;

import fem.member.application.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
@Slf4j
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/{id}/verify/{certificationCode}")
    public ResponseEntity<Void> verifyEmail(@PathVariable Long id, @PathVariable String certificationCode) {
        memberService.verifyEmail(id, certificationCode);

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("/"))
                .build();
    }
}
