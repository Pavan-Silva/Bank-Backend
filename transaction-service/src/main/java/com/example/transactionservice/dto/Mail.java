package com.example.transactionservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Mail {
    private String subject;
    private String message;
    private String receiver;
}
