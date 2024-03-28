package fem.member.domain.exception;

public class CertificationCodeNotMatchedException extends RuntimeException {
    public CertificationCodeNotMatchedException() {
        super("자격 증명에 실패했습니다.");
    }
}
