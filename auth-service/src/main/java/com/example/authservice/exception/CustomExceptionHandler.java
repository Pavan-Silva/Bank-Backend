package com.example.authservice.exception;

import com.example.authservice.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler({AuthenticationException.class})
    public ResponseEntity<ErrorResponse> handleNotFoundException(AuthenticationException exception, HttpServletRequest req) {
        return new ResponseEntity<>(
                ErrorResponse.builder()
                        .status(HttpStatus.FORBIDDEN.value())
                        .timestamp(Instant.now())
                        .error("Authentication Error")
                        .message(exception.getMessage())
                        .path(req.getServletPath())
                        .build()
                , HttpStatus.FORBIDDEN);
    }
}
