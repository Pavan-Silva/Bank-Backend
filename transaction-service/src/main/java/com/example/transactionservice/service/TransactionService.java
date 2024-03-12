package com.example.transactionservice.service;

import com.example.transactionservice.dto.OtpRequest;
import com.example.transactionservice.dto.TransactionRequest;
import com.example.transactionservice.model.Transaction;

public interface TransactionService {

    Transaction findByRefNo(Long refNo);

    Transaction saveDeposit(TransactionRequest transactionRequest);

    Transaction saveWithdrawal(TransactionRequest transactionRequest);

    Transaction saveOnlineTransaction(TransactionRequest transactionRequest);

    Transaction verify(Long refNo, OtpRequest otpRequest);

    void resendOtp(Long refNo);
}
