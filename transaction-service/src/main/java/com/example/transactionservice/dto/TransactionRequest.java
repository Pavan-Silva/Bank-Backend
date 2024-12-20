package com.example.transactionservice.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class TransactionRequest {

    private BigDecimal amount;
    private Long accountId;
}
