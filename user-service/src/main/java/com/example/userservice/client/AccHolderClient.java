package com.example.userservice.client;

import com.example.userservice.dto.response.AccHolder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "accHolder-client", url = "http://localhost:8081/acc-holders")
public interface AccHolderClient {

    @GetMapping("/{id}")
    AccHolder findAccount(@PathVariable int id);
}
