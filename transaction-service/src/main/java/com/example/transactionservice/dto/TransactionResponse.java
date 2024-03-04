package com.example.transactionservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Builder
public class TransactionResponse {

    private Integer refNo;
    private BigDecimal amount;
    private Instant date;
}
