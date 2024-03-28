package fem.member.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MemberCreate {
    private String loginId;
    private String password;
    private String nickname;
}
