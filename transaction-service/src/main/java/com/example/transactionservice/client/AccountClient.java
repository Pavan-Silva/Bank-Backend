package com.example.transactionservice.client;

import com.example.transactionservice.dto.AccountDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PutExchange;

@HttpExchange
public interface AccountClient {

    @GetExchange("/accounts/{id}")
    AccountDetails findAccount(@PathVariable int id);

    @PutExchange("/accounts")
    void updateAccount(@RequestBody AccountDetails accountDetails);
}
