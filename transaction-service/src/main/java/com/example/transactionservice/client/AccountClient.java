package com.example.transactionservice.client;

import com.example.transactionservice.config.OAuthFeignConfig;
import com.example.transactionservice.dto.AccountDetails;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "account-client",
        url = "http://localhost:8081/accounts",
        configuration = OAuthFeignConfig.class
)
public interface AccountClient {

    @GetMapping("/{id}")
    AccountDetails findAccount(@PathVariable int id);

    @PutMapping
    void updateAccount(@RequestBody AccountDetails accountDetails);
}
