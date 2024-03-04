package com.example.transactionservice.controller;

import com.example.transactionservice.dto.OtpRequest;
import com.example.transactionservice.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("transactions/verify")
public class VerificationPageController {

    private final TransactionService transactionService;

    @GetMapping( "/{refNo}")
    public String showVerificationPage(Model model, @PathVariable Integer refNo) {
        if (transactionService.isVerifiableTransaction(refNo)) {
            model.addAttribute("otpRequest", new OtpRequest());
            model.addAttribute("refNo", refNo);
            return "otp-verification";
        }

        return null;
    }

    @GetMapping("/{refNo}/resend")
    public void resend(@PathVariable Integer refNo) {
        transactionService.resendOtp(refNo);
    }
}
