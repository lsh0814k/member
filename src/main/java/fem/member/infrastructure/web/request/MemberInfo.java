package fem.member.infrastructure.web.request;

import fem.member.domain.Member;
import fem.member.domain.vo.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import static lombok.AccessLevel.*;

@Getter
@Builder(access = PRIVATE)
@AllArgsConstructor(access = PRIVATE)
public class MemberInfo {
    private Long id;
    private String loginId;
    private String password;
    private UserRole role;
    public static MemberInfo from(Member member) {
        return MemberInfo.builder()
                .id(member.getId())
                .loginId(member.getLoginId())
                .password(member.getPassword())
                .role(member.getRole())
                .build();
    }
}
