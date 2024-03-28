package fem.member.application;

import fem.member.application.port.MailSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CertificationService {
    private final MailSender mailSender;
    private final String host;

    public CertificationService(MailSender mailSender, @Value("${certification.host}") String host) {
        this.mailSender = mailSender;
        this.host = host;
    }

    public void send(String email, Long id, String certificationCode) {
        String certificationUrl = generateCertificationUrl(id, certificationCode);
        String subject = "이메일 인증";
        String text = "링크 클릭을 통해 가입을 완료해주세요: " + certificationUrl;

        mailSender.send(email, subject, text);
    }
    private String generateCertificationUrl(Long id, String certificationCode) {
        return host + "/api/members/" + id + "/verify?certificationCode=" + certificationCode;
    }
}
