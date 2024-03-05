package com.example.emailservice.dto;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    private Instant timestamp;
    private Integer status;
    private String error;
    private String message;
    private String path;
}
