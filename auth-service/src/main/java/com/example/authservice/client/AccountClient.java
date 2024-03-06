package com.example.authservice.client;

import com.example.authservice.dto.AccHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange
public interface AccountClient {

    @GetExchange("/accounts/holders/{id}")
    AccHolder findAccountHolder(@PathVariable Integer id);
}
