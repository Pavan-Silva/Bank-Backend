package com.example.transactionservice.service;

import com.example.transactionservice.dto.ConfirmationRequest;
import com.example.transactionservice.dto.TransactionInfo;
import com.example.transactionservice.model.Transaction;

import java.util.HashMap;

public interface TransactionService {

    Transaction findByParams(HashMap<String,String> params);

    Transaction save(TransactionInfo transactionInfo);

    void confirm(ConfirmationRequest confirmationRequest);
}
