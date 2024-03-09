package com.example.userservice.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserRequest {

    private String username;
    private String password;
    private Integer accHolderId;
    private String nic;
}
