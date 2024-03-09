package com.example.userservice.dto.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserVerificationRequest {

    private String code;
}
