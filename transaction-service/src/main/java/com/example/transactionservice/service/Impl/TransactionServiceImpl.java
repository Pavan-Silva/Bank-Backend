package com.example.transactionservice.service.Impl;

import com.example.transactionservice.client.AccountClient;
import com.example.transactionservice.dto.*;
import com.example.transactionservice.exception.BadRequestException;
import com.example.transactionservice.exception.NotFoundException;
import com.example.transactionservice.messaging.AccountProducer;
import com.example.transactionservice.messaging.EmailProducer;
import com.example.transactionservice.model.PendingOnlineTransaction;
import com.example.transactionservice.model.Transaction;
import com.example.transactionservice.model.TransactionStatus;
import com.example.transactionservice.repository.PendingTransactionRepository;
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
    private final EmailProducer emailProducer;
    private final AccountProducer accountProducer;
    private final TransactionRepository transactionRepository;
    private final PendingTransactionRepository pendingTransactionRepository;

    @Override
    public Transaction findByRefNo(Long refNo) {
        return transactionRepository.findByRefNo(refNo)
                .orElseThrow(() -> new NotFoundException("Invalid transaction"));
    }

    @Override
    public Transaction saveDeposit(TransactionRequest transactionRequest) {
        String transactionType = transactionRequest.getTransactionType().getName();

        if (!transactionType.equals("Deposit"))
            throw new BadRequestException("Invalid transaction type");

        AccountDetails account = accountClient.findAccount(transactionRequest.getSenderId());

        checkAccountStatus(account);

        BigDecimal accBalance = account.getCurrentBalance().add(transactionRequest.getAmount());

        Transaction pendingTransaction = dtoToTransaction(transactionRequest);
        pendingTransaction.setAccBalance(accBalance);
        Transaction transaction = transactionRepository.save(pendingTransaction);

        account.setCurrentBalance(accBalance);
        accountProducer.sendAccountUpdateMessage(account);
        return transaction;
    }

    @Override
    public Transaction saveWithdrawal(TransactionRequest transactionRequest) {
        String transactionType = transactionRequest.getTransactionType().getName();

        if (!transactionType.equals("Withdrawal"))
            throw new BadRequestException("Invalid transaction type");

        AccountDetails account = accountClient.findAccount(transactionRequest.getSenderId());

        checkAccountStatus(account);
        checkBalance(account.getCurrentBalance(), transactionRequest.getAmount());

        BigDecimal accBalance = account.getCurrentBalance().subtract(transactionRequest.getAmount());

        Transaction pendingTransaction = dtoToTransaction(transactionRequest);
        pendingTransaction.setAccBalance(accBalance);
        Transaction transaction = transactionRepository.save(pendingTransaction);

        account.setCurrentBalance(accBalance);
        accountProducer.sendAccountUpdateMessage(account);
        return transaction;
    }

    @Override
    public Transaction saveOnlineTransaction(TransactionRequest transactionRequest) {
        String transactionType = transactionRequest.getTransactionType().getName();

        if (transactionType.equals("Deposit") || transactionType.equals("Withdrawal"))
            throw new BadRequestException("Invalid transaction type");

        else return handleTransfer(transactionRequest);
    }

    @Override
    public Transaction verify(Long refNo, OtpRequest otpRequest) {
        Transaction transaction = findByRefNo(refNo);

        PendingOnlineTransaction pendingTransaction = pendingTransactionRepository
                .findByTransaction_RefNo(refNo)
                .orElseThrow();

        boolean isPending = transaction.getTransactionStatus().getName().equals("Pending");
        boolean isExpired = pendingTransaction.getExpirationTime().isBefore(Instant.now());

        if (isExpired) {
            handleFailedTransfer(transaction);
            throw new BadRequestException("Transaction Expired");
        }

        if (!isPending) {
            throw new BadRequestException("Transaction already completed");
        }

        AccountDetails sender = accountClient.findAccount(pendingTransaction.getSenderId());
        AccountDetails receiver = accountClient.findAccount(pendingTransaction.getReceiverId());

        if (sender.getAccStatus().getName().equals("Disabled")) {
            handleFailedTransfer(transaction);
            throw new BadRequestException("Account Disabled");
        }

        boolean isOtpValid = otpRequest.getCode().equals(pendingTransaction.getOtp());
        boolean isValidTimePeriod = pendingTransaction.getExpirationTime().isAfter(Instant.now());

        if (!isOtpValid || !isValidTimePeriod) {
            sender.setFailedTransactionAttempts(sender.getFailedTransactionAttempts() + 1);

            String error = "Failed attempt";

            if (sender.getFailedTransactionAttempts() >= 3) {
                sender.setAccStatus(AccountStatus.builder()
                        .id(2)
                        .name("Disabled")
                        .build());

                handleFailedTransfer(transaction);
                error = "Account Disabled";
            }

            accountProducer.sendAccountUpdateMessage(sender);
            throw new BadRequestException(error);
        }

        checkAccountStatus(sender);
        checkAccountStatus(receiver);

        checkBalance(sender.getCurrentBalance(),transaction.getAmount());

        BigDecimal updatedSenderAccBalance = sender.getCurrentBalance().subtract(transaction.getAmount());
        sender.setCurrentBalance(updatedSenderAccBalance);

        BigDecimal updatedReceiverAccBalance = receiver.getCurrentBalance().add(transaction.getAmount());
        receiver.setCurrentBalance(updatedReceiverAccBalance);

        sender.setFailedTransactionAttempts(0);

        TransactionStatus transactionStatus = TransactionStatus.builder()
                .id(3)
                .name("Completed")
                .build();

        transaction.setTransactionStatus(transactionStatus);
        transaction.setAccBalance(updatedSenderAccBalance);
        Transaction completedTransaction = transactionRepository.save(transaction);

        pendingTransactionRepository.delete(pendingTransaction);

        accountProducer.sendAccountUpdateMessage(sender);
        accountProducer.sendAccountUpdateMessage(receiver);

        return completedTransaction;
    }

    @Override
    public void resendOtp(Long refNo) {
        PendingOnlineTransaction pendingOnlineTransaction = pendingTransactionRepository.findByTransaction_RefNo(refNo)
                .orElseThrow(() -> new NotFoundException("Invalid transaction"));

        pendingOnlineTransaction.setOtp(generateRandomOtp());
        pendingOnlineTransaction.setExpirationTime(Instant.now().plusSeconds(60));
        pendingTransactionRepository.save(pendingOnlineTransaction);

        AccountDetails account = accountClient.findAccount(pendingOnlineTransaction.getReceiverId());
        sendVerificationCode(account.getAccHolder().getEmail(), pendingOnlineTransaction.getOtp());
    }

    private Transaction handleTransfer(TransactionRequest transactionRequest) {
        AccountDetails sender = accountClient.findAccount(transactionRequest.getSenderId());
        AccountDetails receiver = accountClient.findAccount(transactionRequest.getReceiverId());

        Transaction transaction = dtoToTransaction(transactionRequest);
        transaction.setAccBalance(sender.getCurrentBalance());

        transactionRepository.save(transaction);
        pendingTransactionRepository.deleteByTransaction(transaction);

        PendingOnlineTransaction pendingOnlineTransaction = pendingTransactionRepository.save(
                PendingOnlineTransaction.builder()
                        .receiverId(receiver.getId())
                        .senderId(sender.getId())
                        .transaction(transaction)
                        .expirationTime(Instant.now().plusSeconds(60))
                        .otp(generateRandomOtp())
                        .build()
        );

        sendVerificationCode(sender.getAccHolder().getEmail(), pendingOnlineTransaction.getOtp());
        return transaction;
    }

    private void handleFailedTransfer(Transaction transaction) {
        TransactionStatus transactionStatusFailed = TransactionStatus.builder()
                .id(4)
                .name("Failed")
                .build();

        transaction.setTransactionStatus(transactionStatusFailed);
        transactionRepository.save(transaction);
    }

    private void checkBalance(BigDecimal balance, BigDecimal amount) {
        if (balance.compareTo(amount) < 0)
            throw new BadRequestException("Insufficient Balance");
    }

    private void checkAccountStatus(AccountDetails accountDetails) {
        if (!accountDetails.getAccStatus().getName().equals("Active"))
            throw new BadRequestException("Account Disabled");
    }

    private void sendVerificationCode(String email, String otp) {
        emailProducer.sendEmailMessage(
                Mail.builder()
                        .subject("Abc Bank OTP")
                        .message("OTP for your current transaction is: " + otp)
                        .receiver(email)
                        .build()
        );
    }

    private String generateRandomOtp() {
        Random random = new Random();
        return String.valueOf(random.nextInt(1000000));
    }

    private Transaction dtoToTransaction(TransactionRequest transactionRequest) {
        TransactionStatus transactionStatus = TransactionStatus.builder()
                .id(1)
                .name("Pending")
                .build();

        return Transaction.builder()
                .date(Instant.now())
                .amount(transactionRequest.getAmount())
                .accountId(transactionRequest.getSenderId())
                .remarks(transactionRequest.getRemarks())
                .transactionType(transactionRequest.getTransactionType())
                .transactionStatus(transactionStatus)
                .build();
    }
}
