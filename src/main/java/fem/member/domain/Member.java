package fem.member.domain;

import fem.member.domain.exception.CertificationCodeNotMatchedException;
import fem.member.domain.vo.UserRole;
import fem.member.domain.vo.MemberStatus;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.EnumType.STRING;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
@EqualsAndHashCode(of = "id")
public class Member {
    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private String loginId;
    private String password;
    private String nickname;
    private String certificationCode;
    @Enumerated(STRING)
    private UserRole role;
    @Enumerated(STRING)
    private MemberStatus status;

    public static Member create(MemberCreate memberCreate, String certificationCode) {
        return Member.builder()
                .loginId(memberCreate.getLoginId())
                .password(memberCreate.getPassword())
                .nickname(memberCreate.getNickname())
                .certificationCode(certificationCode)
                .role(UserRole.USER)
                .status(MemberStatus.PENDING)
                .build();
    }

    public void certificate(String certificationCode) {
        if (!this.certificationCode.equals(certificationCode)) {
          throw new CertificationCodeNotMatchedException();
        }

        this.status = MemberStatus.ACTIVE;
    }
}
