package com.example.accountservice.consumer;

import com.example.accountservice.dto.AccountDetails;
import com.example.accountservice.model.AccHolder;
import com.example.accountservice.model.AccStatus;
import com.example.accountservice.model.Account;
import com.example.accountservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RabbitMQConsumer {

    private final AccountService accountService;

    @RabbitListener(queues = {"${rabbitmq.queue.name}"})
    public void consume(AccountDetails accountDetails) {
        AccHolder accHolder = AccHolder.builder()
                .id(accountDetails.getAccHolder().getId())
                .name(accountDetails.getAccHolder().getName())
                .email(accountDetails.getAccHolder().getEmail())
                .build();

        AccStatus accStatus = AccStatus.builder()
                .id(accountDetails.getAccStatus().getId())
                .name(accountDetails.getAccStatus().getName())
                .build();

        accountService.update(
                Account.builder()
                        .id(accountDetails.getId())
                        .accHolder(accHolder)
                        .accStatus(accStatus)
                        .currentBalance(accountDetails.getCurrentBalance())
                        .failedTransactionAttempts(accountDetails.getFailedTransactionAttempts())
                        .build()
        );
    }
}
