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

    @GetMapping("/{refNo}")
    public Transaction find(@PathVariable Integer refNo) {
        return transactionService.findByRefNo(refNo);
    }

    @PostMapping
    public Transaction save(@RequestBody TransactionRequest transactionRequest) {
        return transactionService.saveDomesticTransaction(transactionRequest);
    }

    @PostMapping("/online")
    public Transaction saveOnlineTransaction(@RequestBody TransactionRequest transactionRequest){
        return transactionService.saveOnlineTransaction(transactionRequest);
    }

    @PostMapping("/online/verify/{refNo}")
    public Transaction verify(@PathVariable Integer refNo, @RequestBody OtpRequest otpRequest) {
        return transactionService.verify(refNo, otpRequest);
    }

    @GetMapping("/online/verify/{refNo}/resend")
    public void resendOtp(@PathVariable Integer refNo) {
        transactionService.resendOtp(refNo);
    }
}