package com.example.transactionservice.controller;

import com.example.transactionservice.dto.OtpRequest;
import com.example.transactionservice.dto.TransactionRequest;
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
        return transactionService.findByRefNo(refNo);
    }

    @PostMapping
    public Transaction save(@RequestBody TransactionRequest transactionRequest){
        return transactionService.save(transactionRequest);
    }

    @PostMapping("/verify/{refNo}")
    public Transaction verify(@PathVariable Integer refNo, @ModelAttribute OtpRequest otpRequest) {
        return transactionService.verify(refNo, otpRequest);
    }

    @GetMapping("/verify/{refNo}/resend")
    public void resendOtp(@PathVariable Integer refNo) {
        transactionService.resendOtp(refNo);
    }
}