package com.example.userservice.client;

import com.example.userservice.dto.AccHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange
public interface AccHolderClient {

    @GetExchange("/accounts/holders/{id}")
    AccHolder findAccount(@PathVariable int id);
}
