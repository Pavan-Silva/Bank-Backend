package com.example.userservice.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordResetRequest {

    private String userId;
    private String currentPassword;
    private String newPassword;
}
