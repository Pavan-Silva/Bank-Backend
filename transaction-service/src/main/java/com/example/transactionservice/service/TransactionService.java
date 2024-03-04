package com.example.transactionservice.service;

import com.example.transactionservice.dto.Otp;
import com.example.transactionservice.dto.TransactionInfo;
import com.example.transactionservice.model.Transaction;

public interface TransactionService {

    boolean isVerifiableTransaction(Integer refNo);

    Transaction save(TransactionInfo transactionInfo);

    boolean verify(Integer refNo, Otp otp);

    void resendOtp(Integer refNo);
}
