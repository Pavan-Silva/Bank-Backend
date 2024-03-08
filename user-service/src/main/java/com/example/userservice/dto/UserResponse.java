package com.example.userservice.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {

    private String userId;
    private String username;
}
