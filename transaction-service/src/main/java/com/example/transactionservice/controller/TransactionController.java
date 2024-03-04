package com.example.transactionservice.controller;

import com.example.transactionservice.dto.TransactionInfo;
import com.example.transactionservice.model.Transaction;
import com.example.transactionservice.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public Transaction find(@RequestParam Integer refNo) {
        return null;
    }

    @PostMapping
    public Transaction save(@RequestBody TransactionInfo transactionInfo){
        return transactionService.save(transactionInfo);
    }
}