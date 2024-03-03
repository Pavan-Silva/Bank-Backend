package com.example.transactionservice.service;

import com.example.transactionservice.dto.Otp;
import com.example.transactionservice.dto.TransactionInfo;
import com.example.transactionservice.model.Transaction;

import java.util.HashMap;

public interface TransactionService {

    Transaction findByParams(HashMap<String,String> params);

    Transaction save(TransactionInfo transactionInfo);

    boolean verify(Integer refNo, Otp otp);

    void resendOtp(Integer refNo);
}
