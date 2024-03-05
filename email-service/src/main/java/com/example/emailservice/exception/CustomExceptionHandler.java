package com.example.emailservice.exception;

import com.example.emailservice.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler({MailException.class})
    public ResponseEntity<ErrorResponse> handleMailException(MailException exception, HttpServletRequest req) {
        return new ResponseEntity<>(
                ErrorResponse.builder()
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .timestamp(Instant.now())
                        .error("Server Error")
                        .message(exception.getMessage())
                        .path(req.getServletPath())
                        .build()
                , HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
