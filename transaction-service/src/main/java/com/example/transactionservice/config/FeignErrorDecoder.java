package com.example.transactionservice.config;

import com.example.transactionservice.dto.ErrorResponse;
import com.example.transactionservice.exception.NotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.InputStream;

@RequiredArgsConstructor
public class FeignErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder errorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        ErrorResponse errorResponse;

        try (InputStream bodyIs = response.body().asInputStream()) {
            ObjectMapper mapper = new ObjectMapper();
            errorResponse = mapper.readValue(bodyIs, ErrorResponse.class);

        } catch (IOException e) {
            return new Exception(e.getMessage());
        }

        if (response.status() == 404) {
            return new NotFoundException(errorResponse.getMessage());
        }

        return errorDecoder.decode(methodKey, response);
    }
}
