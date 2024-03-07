package com.example.userservice.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {

    private String authId;
    private String username;
}
