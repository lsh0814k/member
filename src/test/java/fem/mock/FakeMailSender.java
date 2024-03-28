package fem.mock;

import fem.member.application.port.MailSender;
import lombok.Getter;

@Getter
public class FakeMailSender implements MailSender {
    private String email;
    private String subject;
    private String text;
    @Override
    public void send(String email, String subject, String text) {
       this.email = email;
       this.subject = subject;
       this.text = text;
    }
}
