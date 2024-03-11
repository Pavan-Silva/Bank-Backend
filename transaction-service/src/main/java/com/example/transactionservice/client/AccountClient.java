package com.example.transactionservice.client;

import com.example.transactionservice.dto.AccountDetails;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "account-client", url = "http://localhost:8081/accounts")
public interface AccountClient {

    @GetMapping("/{id}")
    AccountDetails findAccount(@PathVariable int id);
}
