package com.example.emailservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Mail {

    private String subject;
    private String message;
    private String receiver;
}
