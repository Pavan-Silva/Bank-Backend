package com.example.emailservice.controller;

import com.example.emailservice.dto.Mail;
import com.example.emailservice.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/email")
public class MailController {

    private final MailService mailService;

    @PostMapping("/send")
    public void send(@RequestBody Mail mail) {
        mailService.sendMail(mail);
    }
}
