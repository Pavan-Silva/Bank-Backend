package com.example.transactionservice.client;

import com.example.transactionservice.dto.Mail;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange
public interface EmailClient {

    @PostExchange("/email/send")
    void sendMail(@RequestBody Mail mail);
}
