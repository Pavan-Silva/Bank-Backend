package com.example.transactionservice.dto;

import com.example.transactionservice.model.TransactionType;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class TransactionRequest {

    private BigDecimal amount;
    private Integer receiverId;
    private Integer senderId;
    private String remarks;
    private TransactionType transactionType;
}
