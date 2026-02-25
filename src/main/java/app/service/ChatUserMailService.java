package app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatUserMailService {
    private final JavaMailSender mailSender;
    @Value("${app.mail.from}")
    private String from;

    public void sendWelcomeEmail(String toEmail, String username) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(toEmail);
        message.setSubject(String.format("Welcome %s", username));
        message.setText(String.format("Hi %s welcome to chat application", username));

        mailSender.send(message);
    }
}
