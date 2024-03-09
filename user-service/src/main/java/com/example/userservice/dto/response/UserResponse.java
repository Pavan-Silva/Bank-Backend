package com.example.userservice.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {

    private String userId;
    private String username;
}
