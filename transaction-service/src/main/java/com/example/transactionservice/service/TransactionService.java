package com.example.transactionservice.service;

import com.example.transactionservice.dto.OtpRequest;
import com.example.transactionservice.dto.TransactionRequest;
import com.example.transactionservice.dto.TransferRequest;
import com.example.transactionservice.model.Transaction;
import org.springframework.data.domain.Page;

public interface TransactionService {

    Page<Transaction> findAll(int page, int size);

    Page<Transaction> findAllByAccId(int page, int size, Long accId);

    Transaction findByRefNo(Long refNo);

    Transaction saveDeposit(TransactionRequest transactionRequest);

    Transaction saveWithdrawal(TransactionRequest transactionRequest);

    Transaction saveOnlineTransaction(TransferRequest transferRequest);

    Transaction verify(Long refNo, OtpRequest otpRequest);

    void resendOtp(Long refNo);
}
