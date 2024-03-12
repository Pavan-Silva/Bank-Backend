package com.example.transactionservice.service;

import com.example.transactionservice.dto.OtpRequest;
import com.example.transactionservice.dto.TransactionRequest;
import com.example.transactionservice.dto.TransferRequest;
import com.example.transactionservice.model.Transaction;

public interface TransactionService {

    Transaction findByRefNo(Long refNo);

    Transaction saveDeposit(TransactionRequest transactionRequest);

    Transaction saveWithdrawal(TransactionRequest transactionRequest);

    Transaction saveOnlineTransaction(TransferRequest transferRequest);

    Transaction verify(Long refNo, OtpRequest otpRequest);

    void resendOtp(Long refNo);
}
