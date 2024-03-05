package com.example.transactionservice.service.Impl;

import com.example.transactionservice.client.AccountClient;
import com.example.transactionservice.client.EmailClient;
import com.example.transactionservice.dto.*;
import com.example.transactionservice.exception.BadRequestException;
import com.example.transactionservice.exception.NotFoundException;
import com.example.transactionservice.model.Transaction;
import com.example.transactionservice.model.TransactionConfirmation;
import com.example.transactionservice.model.TransactionStatus;
import com.example.transactionservice.repository.TransactionConfirmationRepository;
import com.example.transactionservice.repository.TransactionRepository;
import com.example.transactionservice.service.TransactionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Random;

@Service
@Transactional
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final AccountClient accountClient;
    private final EmailClient emailClient;
    private final TransactionRepository transactionRepository;
    private final TransactionConfirmationRepository transactionConfirmationRepository;

    @Override
    public Transaction findByRefNo(Integer refNo) {
        return transactionRepository.findByRefNo(refNo)
                .orElseThrow(() -> new NotFoundException("Invalid transaction"));
    }

    @Override
    public Transaction save(TransactionRequest transactionRequest) {
        String transactionType = transactionRequest.getTransactionType().getName();

        if (transactionType.equals("Deposit") || transactionType.equals("Withdrawal"))
            return handleMonoTransaction(transactionRequest);

        else
            return handleTransfer(transactionRequest);
    }

    @Override
    public Transaction verify(Integer refNo, OtpRequest otpRequest) {
        Transaction transaction = findByRefNo(refNo);

        boolean isPending = transaction.getTransactionStatus().getName().equals("Pending");
        boolean isNotExpired = transaction.getDate().plusSeconds(60).isAfter(Instant.now());

        if (isPending && isNotExpired) {
            TransactionConfirmation confirmation = transactionConfirmationRepository
                    .findByTransaction_RefNo(refNo)
                    .orElseThrow();

            AccountDetails sender = accountClient.findAccount(transaction.getAccHolderId());
            AccountDetails receiver = accountClient.findAccount(confirmation.getReceiverId());

            boolean isOtpValid = otpRequest.getCode().equals(confirmation.getOtp());
            boolean isValidTimePeriod = confirmation.getExpirationTime().isAfter(Instant.now());

            if (isOtpValid && sender.getFailedTransactionAttempts() <3 && isValidTimePeriod) {
                if (receiver != null) {
                    checkAccountStatus(sender);
                    checkAccountStatus(receiver);

                    checkBalance(sender.getCurrentBalance(),transaction.getAmount());
                    BigDecimal updatedSenderAccBalance = sender.getCurrentBalance().subtract(transaction.getAmount());
                    BigDecimal updatedReceiverAccBalance = receiver.getCurrentBalance().add(transaction.getAmount());

                    receiver.setCurrentBalance(updatedReceiverAccBalance);
                    sender.setCurrentBalance(updatedSenderAccBalance);
                    sender.setFailedTransactionAttempts(0);

                    TransactionStatus transactionStatus = TransactionStatus.builder()
                            .id(3)
                            .name("Completed")
                            .build();

                    transaction.setTransactionStatus(transactionStatus);
                    transaction.setAccBalance(updatedSenderAccBalance);
                    Transaction completedTransaction = transactionRepository.save(transaction);
                    transactionConfirmationRepository.delete(confirmation);

                    accountClient.updateAccount(sender);
                    accountClient.updateAccount(receiver);

                    return completedTransaction;
                }

                return null;

            } else if (sender.getFailedTransactionAttempts() >= 3) {
                sender.setAccStatus(AccountStatus.builder()
                        .id(2)
                        .name("Disabled")
                        .build());

                accountClient.updateAccount(sender);
                throw new BadRequestException("Account Disabled");

            } else {
                sender.setFailedTransactionAttempts(sender.getFailedTransactionAttempts() + 1);
                accountClient.updateAccount(sender);
                throw new BadRequestException("Failed Attempt");
            }
        }

        else {
            TransactionStatus transactionStatusFailed = TransactionStatus.builder()
                    .id(4)
                    .name("Failed")
                    .build();

            transaction.setTransactionStatus(transactionStatusFailed);
            transactionRepository.save(transaction);
            throw new BadRequestException("Transaction Expired");
        }
    }

    @Override
    public void resendOtp(Integer refNo) {
        Transaction transaction = findByRefNo(refNo);
        AccountDetails account = accountClient.findAccount(transaction.getAccHolderId());
        sendVerificationCode(account, transaction);
    }

    private Transaction handleMonoTransaction(TransactionRequest transactionRequest) {
        AccountDetails account = accountClient.findAccount(transactionRequest.getSenderId());

        if (account != null) {
            checkAccountStatus(account);
            BigDecimal accBalance;

            if (transactionRequest.getTransactionType().getName().equals("Withdrawal")) {
                checkBalance(account.getCurrentBalance(), transactionRequest.getAmount());
                accBalance = account.getCurrentBalance().subtract(transactionRequest.getAmount());
                account.setCurrentBalance(accBalance);
            }

            else if (transactionRequest.getTransactionType().getName().equals("Deposit")) {
                accBalance = account.getCurrentBalance().add(transactionRequest.getAmount());
                account.setCurrentBalance(accBalance);
            }

            else throw new RuntimeException();

            Transaction pendingTransaction = dtoToTransaction(transactionRequest);
            pendingTransaction.setAccBalance(accBalance);
            Transaction transaction = transactionRepository.save(pendingTransaction);

            accountClient.updateAccount(account);
            return transaction;
        }

        else throw new RuntimeException("Invalid sender");
    }

    private Transaction handleTransfer(TransactionRequest transactionRequest) {
        AccountDetails account = accountClient.findAccount(transactionRequest.getSenderId());

        Transaction pendingTransaction = dtoToTransaction(transactionRequest);
        pendingTransaction.setAccBalance(account.getCurrentBalance());
        Transaction transaction = transactionRepository.save(pendingTransaction);

        sendVerificationCode(account, transaction);
        return transaction;
    }

    private void checkBalance(BigDecimal balance, BigDecimal amount) {
        if (balance.compareTo(amount) < 0)
            throw new RuntimeException("Insufficient Balance");
    }

    private void checkAccountStatus(AccountDetails accountDetails) {
        if (!accountDetails.getAccStatus().getName().equals("Active"))
            throw new RuntimeException("Account Disabled");
    }

    private void sendVerificationCode(AccountDetails account, Transaction transaction) {
        transactionConfirmationRepository.deleteByTransaction(transaction);

        Random random = new Random();

        TransactionConfirmation confirmation = transactionConfirmationRepository.save(
                TransactionConfirmation.builder()
                        .receiverId(account.getId())
                        .transaction(transaction)
                        .expirationTime(Instant.now().plusSeconds(60))
                        .otp(String.valueOf(random.nextInt(1000000)))
                        .build()
        );

        emailClient.sendMail(
                Mail.builder()
                        .subject("Abc Bank OTP")
                        .message(account.getAccHolder().getName() + ", OTP for your current transaction is: " + confirmation.getOtp())
                        .receiver(account.getAccHolder().getEmail())
                        .build()
        );
    }

    private Transaction dtoToTransaction(TransactionRequest transactionRequest) {
        TransactionStatus transactionStatus = TransactionStatus.builder()
                .id(1)
                .name("Pending")
                .build();

        return Transaction.builder()
                .date(Instant.now())
                .amount(transactionRequest.getAmount())
                .accHolderId(transactionRequest.getSenderId())
                .description(transactionRequest.getDescription())
                .transactionType(transactionRequest.getTransactionType())
                .transactionStatus(transactionStatus)
                .build();
    }
}
