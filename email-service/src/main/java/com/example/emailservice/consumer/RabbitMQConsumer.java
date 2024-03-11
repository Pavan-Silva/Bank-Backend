package com.example.emailservice.consumer;

import com.example.emailservice.dto.Mail;
import com.example.emailservice.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RabbitMQConsumer {

    private final MailService mailService;

    @RabbitListener(queues = {"${rabbitmq.queue.name}"})
    public void consume(Mail mail) {
        mailService.sendMail(mail);
    }
}
