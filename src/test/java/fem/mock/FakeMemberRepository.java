package fem.mock;

import fem.member.application.port.MemberRepository;
import fem.member.domain.Member;
import fem.member.domain.exception.ResourceNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class FakeMemberRepository implements MemberRepository {
    private final List<Member> datas = new ArrayList<>();
    private Long generatedValue = 1L;

    @Override
    public Member save(Member member) {
        if (member.getId() == null) {
            Member newMember = Member.builder()
                    .id(generatedValue++)
                    .loginId(member.getLoginId())
                    .password(member.getPassword())
                    .nickname(member.getNickname())
                    .status(member.getStatus())
                    .role(member.getRole())
                    .certificationCode(member.getCertificationCode())
                    .build();
            datas.add(newMember);

            return newMember;
        } else {
            datas.removeIf(item -> Objects.equals(item.getId(), member.getId()));
            datas.add(member);

            return member;
        }
    }

    @Override
    public Optional<Member> findByLoginId(String loginId) {
        return datas.stream().filter(item -> item.getLoginId().equals(loginId)).findAny();
    }

    @Override
    public Member getById(Long id) {
        return datas.stream()
                .filter(item -> Objects.equals(item.getId(), id))
                .findAny()
                .orElseThrow(() -> new ResourceNotFoundException("Member", id));
    }
}
