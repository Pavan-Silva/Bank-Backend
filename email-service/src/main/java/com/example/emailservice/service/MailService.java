package com.example.emailservice.service;

import com.example.emailservice.dto.Mail;
import com.example.emailservice.exception.MailException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String sender;

    public void sendMail(Mail mail) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(sender);
            message.setSubject(mail.getSubject());
            message.setText(mail.getMessage());
            message.setTo(mail.getReceiver());
            mailSender.send(message);

        } catch (Exception e) {
            throw new MailException("Couldn't send email");
        }
    }
}
