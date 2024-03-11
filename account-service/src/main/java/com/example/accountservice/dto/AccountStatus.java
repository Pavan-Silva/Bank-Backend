package com.example.accountservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AccountStatus {

    private Integer id;
    private String name;
}