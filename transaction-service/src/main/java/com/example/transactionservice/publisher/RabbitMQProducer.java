package com.example.transactionservice.publisher;

import com.example.transactionservice.dto.AccountDetails;
import com.example.transactionservice.dto.Mail;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RabbitMQProducer {

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.routing.email-key}")
    private String emailRoutingKey;

    @Value("${rabbitmq.routing.accounts-key}")
    private String accountsRoutingKey;

    private final RabbitTemplate rabbitTemplate;

    public void sendEmailMessage(Mail mail) {
        rabbitTemplate.convertAndSend(exchange, emailRoutingKey, mail);
    }

    public void sendAccountUpdateMessage(AccountDetails accountDetails) {
        rabbitTemplate.convertAndSend(exchange, accountsRoutingKey, accountDetails);
    }
}
