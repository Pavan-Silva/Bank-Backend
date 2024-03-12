package com.example.accountservice.consumer;

import com.example.accountservice.model.Account;
import com.example.accountservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RabbitMQConsumer {

    private final AccountService accountService;

    @RabbitListener(queues = {"${rabbitmq.queue.name}"})
    public void consume(Account account) {
        accountService.update(account);
    }
}
