package com.example.transactionservice.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class TransferRequest {

    private BigDecimal amount;
    private Long receiverId;
    private Long senderId;
    private String remarks;
}
