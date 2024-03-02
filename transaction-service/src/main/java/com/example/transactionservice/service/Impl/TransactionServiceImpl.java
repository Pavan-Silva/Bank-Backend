package com.example.transactionservice.service.Impl;

import com.example.transactionservice.client.AccountClient;
import com.example.transactionservice.dto.AccountDetails;
import com.example.transactionservice.model.Transaction;
import com.example.transactionservice.repository.TransactionRepository;
import com.example.transactionservice.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final AccountClient accountClient;
    private final TransactionRepository transactionRepository;

    @Override
    public Transaction findByParams(HashMap<String,String> params) {
        return null;
    }

    @Override
    public Transaction save(Transaction transaction) {
        AccountDetails sender = accountClient.findAccount(transaction.getSenderId());
        AccountDetails receiver = accountClient.findAccount(transaction.getReceiverId());

        if (sender.getId() != null && receiver.getId() != null) {
            BigDecimal updatedSenderAccBalance;
            BigDecimal updatedReceiverAccBalance;

            if (transaction.getTransactionType().getId().equals(3))
                updatedSenderAccBalance = sender.getCurrentBalance().add(transaction.getAmount());

            else {
                updatedSenderAccBalance = sender.getCurrentBalance().subtract(transaction.getAmount());
                updatedReceiverAccBalance = receiver.getCurrentBalance().add(transaction.getAmount());
                receiver.setCurrentBalance(updatedReceiverAccBalance);
            }

            sender.setCurrentBalance(updatedSenderAccBalance);

            Transaction transactionResult = transactionRepository.save(transaction);
            accountClient.updateAccount(sender);
            accountClient.updateAccount(receiver);
            return transactionResult;
        }

        else throw new RuntimeException();
    }
}
