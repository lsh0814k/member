package fem.member.application.port;

public interface MailSender {
    void send(String email, String subject, String text);
}
