package com.example.transactionservice.client;

import com.example.transactionservice.dto.Mail;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "emailClient", url = "http://localhost:8083/email")
public interface EmailClient {

    @PostMapping("/send")
    void sendMail(@RequestBody Mail mail);
}
