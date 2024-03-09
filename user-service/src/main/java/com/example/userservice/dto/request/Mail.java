package com.example.userservice.dto.request;

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
