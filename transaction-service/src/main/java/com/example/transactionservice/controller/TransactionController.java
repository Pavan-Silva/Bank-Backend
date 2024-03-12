package com.example.transactionservice.controller;

import com.example.transactionservice.dto.OtpRequest;
import com.example.transactionservice.dto.TransactionRequest;
import com.example.transactionservice.dto.TransferRequest;
import com.example.transactionservice.model.Transaction;
import com.example.transactionservice.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public Page<Transaction> findAll(@RequestParam int page, @RequestParam int size, @RequestParam(required = false) Long accId) {
        if (accId != null) {
            return transactionService.findAllByAccId(page, size, accId);
        }

        return transactionService.findAll(page, size);
    }

    @GetMapping("/{refNo}")
    public Transaction find(@PathVariable Long refNo) {
        return transactionService.findByRefNo(refNo);
    }

    @PostMapping("/deposit")
    public Transaction saveDeposit(@RequestBody TransactionRequest transactionRequest) {
        return transactionService.saveDeposit(transactionRequest);
    }

    @PostMapping("/withdraw")
    public Transaction saveWithdraw(@RequestBody TransactionRequest transactionRequest) {
        return transactionService.saveWithdrawal(transactionRequest);
    }

    @PostMapping("/online")
    public Transaction saveOnlineTransaction(@RequestBody TransferRequest transferRequest){
        return transactionService.saveOnlineTransaction(transferRequest);
    }

    @PostMapping("/verify/{refNo}")
    public Transaction verify(@PathVariable Long refNo, @RequestBody OtpRequest otpRequest) {
        return transactionService.verify(refNo, otpRequest);
    }

    @GetMapping("/verify/{refNo}/resend")
    public void resendOtp(@PathVariable Long refNo) {
        transactionService.resendOtp(refNo);
    }
}