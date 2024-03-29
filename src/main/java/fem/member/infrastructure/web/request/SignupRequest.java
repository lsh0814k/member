package fem.member.infrastructure.web.request;

import fem.member.domain.MemberCreate;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignupRequest {
    @NotBlank
    private String loginId;
    @NotBlank
    private String password;
    @NotBlank
    private String passwordConf;
    @NotBlank
    private String nickname;

    public MemberCreate toModel() {
        return MemberCreate.builder()
                .loginId(loginId)
                .password(password)
                .password(passwordConf)
                .nickname(nickname)
                .build();
    }
}
