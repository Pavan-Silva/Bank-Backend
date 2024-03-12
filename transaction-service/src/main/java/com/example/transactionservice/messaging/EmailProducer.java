package com.example.transactionservice.messaging;

import com.example.transactionservice.dto.Mail;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailProducer {

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.routing.email-key}")
    private String emailRoutingKey;

    private final RabbitTemplate rabbitTemplate;

    public void sendEmailMessage(Mail mail) {
        rabbitTemplate.convertAndSend(exchange, emailRoutingKey, mail);
    }
}
