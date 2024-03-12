package com.example.userservice.exception;

import com.example.userservice.dto.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler({NotFoundException.class})
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException exception) {
        return new ResponseEntity<>(
                ErrorResponse.builder()
                        .code(404)
                        .error("Not found")
                        .message(exception.getMessage())
                        .build()
                , HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({BadRequestException.class})
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException exception) {
        return new ResponseEntity<>(
                ErrorResponse.builder()
                        .code(400)
                        .error("Bad Request")
                        .message(exception.getMessage())
                        .build()
                , HttpStatus.BAD_REQUEST);
    }
}
