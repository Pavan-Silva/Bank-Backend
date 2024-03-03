package com.example.transactionservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ConfirmationRequest {
    private Integer refNo;
    private String otp;
}
