package com.example.transactionservice.service;

import com.example.transactionservice.dto.OtpRequest;
import com.example.transactionservice.dto.TransactionRequest;
import com.example.transactionservice.model.Transaction;

public interface TransactionService {

    boolean isVerifiableTransaction(Integer refNo);

    Transaction save(TransactionRequest transactionRequest);

    Transaction verify(Integer refNo, OtpRequest otpRequest);

    void resendOtp(Integer refNo);
}
