package com.example.emailservice.service;

import com.example.emailservice.dto.Mail;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    public void sendMail(Mail mail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("no-reply@abcbank.lk");
        message.setSubject(mail.getSubject());
        message.setText(mail.getMessage());
        message.setTo(mail.getReceiver());

        mailSender.send(message);
    }
}
