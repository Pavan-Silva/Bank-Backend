package com.example.transactionservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AccountDetails {

    private Integer id;
    private BigDecimal currentBalance;
    private AccountStatus accStatus;
    private AccHolder accHolder;
    private Integer failedTransactionAttempts;
}
