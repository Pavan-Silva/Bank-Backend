package com.example.transactionservice.service.Impl;

import com.example.transactionservice.client.AccountClient;
import com.example.transactionservice.dto.AccountDetails;
import com.example.transactionservice.dto.AccountStatus;
import com.example.transactionservice.dto.ConfirmationRequest;
import com.example.transactionservice.dto.TransactionInfo;
import com.example.transactionservice.model.Transaction;
import com.example.transactionservice.model.TransactionConfirmation;
import com.example.transactionservice.model.TransactionStatus;
import com.example.transactionservice.repository.TransactionConfirmationRepository;
import com.example.transactionservice.repository.TransactionRepository;
import com.example.transactionservice.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final AccountClient accountClient;
    private final TransactionRepository transactionRepository;
    private final TransactionConfirmationRepository transactionConfirmationRepository;

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

    @Override
    public void confirm(ConfirmationRequest confirmationRequest) {
        TransactionConfirmation confirmation = transactionConfirmationRepository
                .findByTransactionsRefNo_RefNo(confirmationRequest.getRefNo())
                .orElseThrow();

        Transaction transaction = transactionRepository.findByRefNo(confirmationRequest.getRefNo())
                .orElseThrow();

        AccountDetails sender = accountClient.findAccount(transaction.getAccHolderId());
        AccountDetails receiver = accountClient.findAccount(confirmation.getReceiverId());

        if (confirmationRequest.getOtp().equals(confirmation.getOtp()) && confirmation.getAttempt() <=3) {
            if (sender != null && receiver != null) {
                checkAccountStatus(sender);
                checkAccountStatus(receiver);

                checkBalance(sender.getCurrentBalance(),transaction.getAmount());
                BigDecimal updatedSenderAccBalance = sender.getCurrentBalance().subtract(transaction.getAmount());
                BigDecimal updatedReceiverAccBalance = receiver.getCurrentBalance().add(transaction.getAmount());

                receiver.setCurrentBalance(updatedReceiverAccBalance);
                sender.setCurrentBalance(updatedSenderAccBalance);

                TransactionStatus transactionStatus = TransactionStatus.builder()
                        .id(3)
                        .name("Completed")
                        .build();

                transaction.setTransactionStatus(transactionStatus);
                transactionRepository.save(transaction);
                transactionConfirmationRepository.delete(confirmation);

                accountClient.updateAccount(sender);
                accountClient.updateAccount(receiver);
            }

            else throw new RuntimeException();

        } else if (confirmation.getAttempt() > 3) {
            sender.setAccStatus(AccountStatus.builder()
                    .id(2)
                    .name("Disabled")
                    .build());

            accountClient.updateAccount(sender);
            throw new RuntimeException();

        } else  {
            confirmation.setAttempt(confirmation.getAttempt() + 1);
            transactionConfirmationRepository.save(confirmation);
            throw new RuntimeException();
        }
    }

    private Transaction handleMonoTransaction(TransactionInfo transactionInfo) {
        AccountDetails account = accountClient.findAccount(transactionInfo.getSenderId());

        if (account != null) {
            checkAccountStatus(account);
            BigDecimal accBalance;

            if (transactionInfo.getTransactionType().getName().equals("Withdrawal")) {
                checkBalance(account.getCurrentBalance(), transactionInfo.getAmount());
                accBalance = account.getCurrentBalance().subtract(transactionInfo.getAmount());
                account.setCurrentBalance(accBalance);
            }

            else if (transactionInfo.getTransactionType().getName().equals("Deposit")) {
                accBalance = account.getCurrentBalance().add(transactionInfo.getAmount());
                account.setCurrentBalance(accBalance);
            }

            else throw new RuntimeException();

            Transaction pendingTransaction = dtoToTransaction(transactionInfo);
            pendingTransaction.setAccBalance(accBalance);
            Transaction transaction = transactionRepository.save(pendingTransaction);

            accountClient.updateAccount(account);
            return transaction;
        }

        else throw new RuntimeException();
    }

    private Transaction handleTransfer(TransactionInfo transactionInfo) {
        Transaction pendingTransaction = dtoToTransaction(transactionInfo);
        pendingTransaction.setAccBalance(BigDecimal.ZERO);
        Transaction transaction = transactionRepository.save(pendingTransaction);

        Random random = new Random();

        transactionConfirmationRepository.save(
                TransactionConfirmation.builder()
                        .receiverId(transactionInfo.getReceiverId())
                        .transactionsRefNo(transaction)
                        .attempt(1)
                        .otp(String.valueOf(random.nextInt(1000000)))
                        .build()
        );

        return transaction;
    }

    private void checkBalance(BigDecimal balance, BigDecimal amount) {
        if (balance.compareTo(amount) < 0)
            throw new RuntimeException();
    }

    private void checkAccountStatus(AccountDetails accountDetails) {
        if (!accountDetails.getAccStatus().getName().equals("Active"))
            throw new RuntimeException();
    }

    private Transaction dtoToTransaction(TransactionInfo transactionInfo) {
        TransactionStatus transactionStatus = TransactionStatus.builder()
                .id(1)
                .name("Pending")
                .build();

        return Transaction.builder()
                .date(Instant.now())
                .amount(transactionInfo.getAmount())
                .accHolderId(transactionInfo.getSenderId())
                .description(transactionInfo.getDescription())
                .transactionType(transactionInfo.getTransactionType())
                .transactionStatus(transactionStatus)
                .build();
    }
}
