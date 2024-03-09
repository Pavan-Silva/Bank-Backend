package com.example.userservice.client;

import com.example.userservice.dto.request.Mail;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "email-client", url = "http://localhost:8083/email")
public interface EmailClient {

    @PostMapping("/send")
    void sendMail(@RequestBody Mail mail);
}
