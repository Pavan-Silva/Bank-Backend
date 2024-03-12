package com.example.transactionservice.messaging;

import com.example.transactionservice.dto.AccountDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountProducer {

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.routing.accounts-key}")
    private String accountsRoutingKey;

    private final RabbitTemplate rabbitTemplate;

    public void sendAccountUpdateMessage(AccountDetails accountDetails) {
        rabbitTemplate.convertAndSend(exchange, accountsRoutingKey, accountDetails);
    }
}
