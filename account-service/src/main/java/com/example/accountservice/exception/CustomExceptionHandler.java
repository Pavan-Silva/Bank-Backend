package com.example.accountservice.exception;

import com.example.accountservice.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler({NotFoundException.class})
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException exception, HttpServletRequest req) {
        return new ResponseEntity<>(
                ErrorResponse.builder()
                        .status(HttpStatus.NOT_FOUND.value())
                        .timestamp(Instant.now())
                        .error("Not found")
                        .message(exception.getMessage())
                        .path(req.getServletPath())
                        .build()
                , HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({BadRequestException.class})
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException exception, HttpServletRequest req) {
        return new ResponseEntity<>(
                ErrorResponse.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .timestamp(Instant.now())
                        .error("Bad Request")
                        .message(exception.getMessage())
                        .path(req.getServletPath())
                        .build()
                , HttpStatus.BAD_REQUEST);
    }
}
