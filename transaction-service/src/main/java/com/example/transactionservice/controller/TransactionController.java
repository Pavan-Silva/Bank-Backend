package com.example.transactionservice.controller;

import com.example.transactionservice.dto.TransactionInfo;
import com.example.transactionservice.model.Transaction;
import com.example.transactionservice.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequiredArgsConstructor
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public Transaction find(@RequestParam HashMap<String,String> params) {
        return transactionService.findByParams(params);
    }

    @PostMapping
    public Transaction save(@RequestBody TransactionInfo transactionInfo){
        return transactionService.save(transactionInfo);
    }
}
