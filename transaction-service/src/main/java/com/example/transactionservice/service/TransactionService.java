package com.example.transactionservice.service;

import com.example.transactionservice.dto.OtpRequest;
import com.example.transactionservice.dto.TransactionRequest;
import com.example.transactionservice.model.Transaction;

public interface TransactionService {

    Transaction findByRefNo(Integer refNo);

    Transaction saveDomesticTransaction(TransactionRequest transactionRequest);

    Transaction saveOnlineTransaction(TransactionRequest transactionRequest);

    Transaction verify(Integer refNo, OtpRequest otpRequest);

    void resendOtp(Integer refNo);
}
