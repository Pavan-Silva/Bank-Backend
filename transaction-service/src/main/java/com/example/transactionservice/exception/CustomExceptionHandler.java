package com.example.transactionservice.exception;

import com.example.transactionservice.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler({NotFoundException.class})
    public ErrorResponse handleNotFoundException(NotFoundException exception, HttpServletRequest req) {
        return ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .timestamp(Instant.now())
                .error("Not found")
                .message(exception.getMessage())
                .path(req.getServletPath())
                .build();
    }

    @ExceptionHandler({BadRequestException.class})
    public ErrorResponse handleBadRequestException(BadRequestException exception, HttpServletRequest req) {
        return ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .timestamp(Instant.now())
                .error("Bad Request")
                .message(exception.getMessage())
                .path(req.getServletPath())
                .build();
    }
}
