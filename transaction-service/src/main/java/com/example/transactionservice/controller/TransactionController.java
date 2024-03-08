package com.example.transactionservice.controller;

import com.example.transactionservice.dto.OtpRequest;
import com.example.transactionservice.dto.TransactionRequest;
import com.example.transactionservice.model.Transaction;
import com.example.transactionservice.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/{refNo}")
    @PreAuthorize("hasRole('STAFF_MEMBER')")
    public Transaction find(@PathVariable Integer refNo) {
        return transactionService.findByRefNo(refNo);
    }

    @PostMapping
    @PreAuthorize("hasRole('STAFF_MEMBER')")
    public Transaction save(@RequestBody TransactionRequest transactionRequest) {
        return transactionService.saveDomesticTransaction(transactionRequest);
    }

    @PostMapping("/online")
    @PreAuthorize("hasRole('USER')")
    public Transaction saveOnlineTransaction(@RequestBody TransactionRequest transactionRequest){
        return transactionService.saveOnlineTransaction(transactionRequest);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/verify/{refNo}")
    public Transaction verify(@PathVariable Integer refNo, @RequestBody OtpRequest otpRequest) {
        return transactionService.verify(refNo, otpRequest);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/verify/{refNo}/resend")
    public void resendOtp(@PathVariable Integer refNo) {
        transactionService.resendOtp(refNo);
    }
}