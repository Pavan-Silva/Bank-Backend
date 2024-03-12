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
import com.example.transactionservice.model.TransactionType;
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
        AccountDetails account = accountClient.findAccount(transactionRequest.getAccountId());

        BigDecimal accBalance = account.getCurrentBalance().add(transactionRequest.getAmount());

        TransactionType transactionType = TransactionType.builder()
                .id(3)
                .name("Deposit")
                .build();

        Transaction transaction = dtoToTransaction(transactionRequest);
        transaction.setTransactionType(transactionType);
        transaction.setAccBalance(accBalance);

        Transaction transactionResult = transactionRepository.save(transaction);

        account.setCurrentBalance(accBalance);

        accountProducer.sendAccountUpdateMessage(account);
        emailProducer.sendEmailMessage(
                Mail.builder()
                        .subject("Abc Bank Transaction")
                        .receiver(account.getAccHolder().getEmail())
                        .message(
                                "You have successfully received: " + transactionResult.getAmount() + " LKR, " +
                                "Your current account balance is: " + transactionResult.getAccBalance() + " LKR."
                        )
                        .build()
        );
        return transactionResult;
    }

    @Override
    public Transaction saveWithdrawal(TransactionRequest transactionRequest) {
        AccountDetails account = accountClient.findAccount(transactionRequest.getAccountId());

        checkAccountStatus(account);
        checkBalance(account.getCurrentBalance(), transactionRequest.getAmount());

        BigDecimal accBalance = account.getCurrentBalance().subtract(transactionRequest.getAmount());

        TransactionType transactionType = TransactionType.builder()
                .id(4)
                .name("Withdrawal")
                .build();

        Transaction transaction = dtoToTransaction(transactionRequest);
        transaction.setTransactionType(transactionType);
        transaction.setAccBalance(accBalance);

        Transaction transactionResult = transactionRepository.save(transaction);

        account.setCurrentBalance(accBalance);

        accountProducer.sendAccountUpdateMessage(account);
        emailProducer.sendEmailMessage(
                Mail.builder()
                        .subject("Abc Bank Transaction")
                        .receiver(account.getAccHolder().getEmail())
                        .message(
                                "You have successfully withdrawn: " + transactionResult.getAmount() + " LKR, " +
                                "Your current account balance is: " + transactionResult.getAccBalance() + " LKR."
                        )
                        .build()
        );
        return transactionResult;
    }

    @Override
    public Transaction saveOnlineTransaction(TransferRequest transferRequest) {
        return handleTransfer(transferRequest);
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

        AccountDetails sender = accountClient.findAccount(transaction.getMainAccountId());
        AccountDetails receiver = accountClient.findAccount(transaction.getReceiverAccountId());

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

        emailProducer.sendEmailMessage(
                Mail.builder()
                        .subject("ABC Bank Transaction")
                        .receiver(sender.getAccHolder().getEmail())
                        .message(
                                "You have successfully transferred: " + transaction.getAmount() + " LKR, " +
                                        "Your current account balance is: " + sender.getCurrentBalance() + " LKR."
                        )
                        .build()
        );

        emailProducer.sendEmailMessage(
                Mail.builder()
                        .subject("ABC Bank Transaction")
                        .receiver(receiver.getAccHolder().getEmail())
                        .message(
                                "You have successfully received: " + transaction.getAmount() + " LKR, " +
                                        "Your current account balance is: " + receiver.getCurrentBalance() + " LKR."
                        )
                        .build()
        );

        return completedTransaction;
    }

    @Override
    public void resendOtp(Long refNo) {
        Transaction transaction = findByRefNo(refNo);

        PendingOnlineTransaction pendingOnlineTransaction = pendingTransactionRepository.findByTransaction_RefNo(refNo)
                .orElseThrow(() -> new NotFoundException("Invalid transaction"));

        pendingOnlineTransaction.setOtp(generateRandomOtp());
        pendingOnlineTransaction.setExpirationTime(Instant.now().plusSeconds(60));
        pendingTransactionRepository.save(pendingOnlineTransaction);

        AccountDetails account = accountClient.findAccount(transaction.getReceiverAccountId());
        sendVerificationCode(account.getAccHolder().getEmail(), pendingOnlineTransaction.getOtp());
    }

    private Transaction handleTransfer(TransferRequest transferRequest) {
        AccountDetails sender = accountClient.findAccount(transferRequest.getSenderId());

        Transaction transaction = dtoToTransfer(transferRequest);
        transactionRepository.save(transaction);
        pendingTransactionRepository.deleteByTransaction(transaction);

        PendingOnlineTransaction pendingOnlineTransaction = pendingTransactionRepository.save(
                PendingOnlineTransaction.builder()
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
                .id(3)
                .name("Completed")
                .build();

        return Transaction.builder()
                .date(Instant.now())
                .transactionStatus(transactionStatus)
                .amount(transactionRequest.getAmount())
                .mainAccountId(transactionRequest.getAccountId())
                .build();
    }

    private Transaction dtoToTransfer(TransferRequest transferRequest) {
        TransactionStatus transactionStatus = TransactionStatus.builder()
                .id(1)
                .name("Pending")
                .build();

        TransactionType transactionType = TransactionType.builder()
                .id(2)
                .name("Transfer")
                .build();

        return Transaction.builder()
                .date(Instant.now())
                .amount(transferRequest.getAmount())
                .mainAccountId(transferRequest.getSenderId())
                .receiverAccountId(transferRequest.getReceiverId())
                .remarks(transferRequest.getRemarks())
                .transactionType(transactionType)
                .transactionStatus(transactionStatus)
                .build();
    }
}
