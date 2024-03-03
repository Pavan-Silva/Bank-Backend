package com.example.transactionservice.service.Impl;

import com.example.transactionservice.client.AccountClient;
import com.example.transactionservice.dto.AccountDetails;
import com.example.transactionservice.dto.TransactionInfo;
import com.example.transactionservice.model.Transaction;
import com.example.transactionservice.model.TransactionStatus;
import com.example.transactionservice.repository.TransactionRepository;
import com.example.transactionservice.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
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
    public Transaction save(TransactionInfo transactionInfo) {
        String transactionType = transactionInfo.getTransactionType().getName();

        if (transactionType.equals("Deposit") || transactionType.equals("Withdrawal"))
            return handleMonoTransaction(transactionInfo);

        else
            return handleTransfer(transactionInfo);
    }

    private Transaction handleMonoTransaction(TransactionInfo transactionInfo) {
        AccountDetails sender = accountClient.findAccount(transactionInfo.getSenderId());

        if (sender != null) {
            checkAccountStatus(sender);
            BigDecimal accBalance;

            if (transactionInfo.getTransactionType().getName().equals("Withdrawal")) {
                checkBalance(sender.getCurrentBalance(), transactionInfo.getAmount());
                accBalance = sender.getCurrentBalance().subtract(transactionInfo.getAmount());
                sender.setCurrentBalance(accBalance);
            }

            else if (transactionInfo.getTransactionType().getName().equals("Deposit")) {
                accBalance = sender.getCurrentBalance().add(transactionInfo.getAmount());
                sender.setCurrentBalance(accBalance);
            }

            else throw new RuntimeException();

            Transaction pendingTransaction = dtoToTransaction(transactionInfo, accBalance);
            Transaction transaction = transactionRepository.save(pendingTransaction);

            accountClient.updateAccount(sender);
            return transaction;
        }

        else throw new RuntimeException();
    }

    private Transaction handleTransfer(TransactionInfo transactionInfo) {
        AccountDetails sender = accountClient.findAccount(transactionInfo.getSenderId());
        AccountDetails receiver = accountClient.findAccount(transactionInfo.getReceiverId());

        if (sender != null && receiver != null) {
            checkAccountStatus(sender);
            checkAccountStatus(receiver);

            checkBalance(sender.getCurrentBalance(),transactionInfo.getAmount());
            BigDecimal updatedSenderAccBalance = sender.getCurrentBalance().subtract(transactionInfo.getAmount());
            BigDecimal updatedReceiverAccBalance = receiver.getCurrentBalance().add(transactionInfo.getAmount());

            receiver.setCurrentBalance(updatedReceiverAccBalance);
            sender.setCurrentBalance(updatedSenderAccBalance);

            Transaction pendingTransaction = dtoToTransaction(transactionInfo, updatedSenderAccBalance);
            Transaction transaction = transactionRepository.save(pendingTransaction);

            accountClient.updateAccount(sender);
            accountClient.updateAccount(receiver);
            return transaction;
        }

        else throw new RuntimeException();
    }

    private void checkBalance(BigDecimal balance, BigDecimal amount) {
        if (balance.compareTo(amount) < 0)
            throw new RuntimeException();
    }

    private void checkAccountStatus(AccountDetails accountDetails) {
        if (!accountDetails.getAccStatus().getName().equals("Active"))
            throw new RuntimeException();
    }

    private Transaction dtoToTransaction(TransactionInfo transactionInfo, BigDecimal balance) {
        TransactionStatus transactionStatus = TransactionStatus.builder()
                .id(1)
                .name("Pending")
                .build();

        return Transaction.builder()
                .date(Instant.now())
                .accBalance(balance)
                .amount(transactionInfo.getAmount())
                .accHolderId(transactionInfo.getSenderId())
                .description(transactionInfo.getDescription())
                .transactionType(transactionInfo.getTransactionType())
                .transactionStatus(transactionStatus)
                .build();
    }
}
