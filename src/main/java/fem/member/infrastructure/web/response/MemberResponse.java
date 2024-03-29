package fem.member.infrastructure.web.response;

import fem.member.domain.Member;
import fem.member.domain.vo.MemberStatus;
import fem.member.domain.vo.UserRole;
import lombok.*;

import static lombok.AccessLevel.*;

@Getter
@AllArgsConstructor(access = PRIVATE)
@Builder(access = PRIVATE)
public class MemberResponse {
    private Long id;
    private String loginId;
    private String nickname;
    private UserRole role;
    private MemberStatus status;


    public static MemberResponse from(Member member) {
        return MemberResponse.builder()
                .id(member.getId())
                .loginId(member.getLoginId())
                .nickname(member.getNickname())
                .role(member.getRole())
                .status(member.getStatus())
                .build();
    }
}
