package com.example.transactionservice.controller;

import com.example.transactionservice.dto.Otp;
import com.example.transactionservice.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("transactions/verify")
public class VerificationPageController {

    private final TransactionService transactionService;

    @GetMapping( "/{refNo}")
    public String showVerificationPage(Model model, @PathVariable Integer refNo) {
        if (transactionService.isVerifiableTransaction(refNo)) {
            model.addAttribute("otp", new Otp());
            model.addAttribute("refNo", refNo);
            return "otp-verification";
        }

        return null;
    }

    @PostMapping("/{refNo}")
    public String verify(@PathVariable Integer refNo, @ModelAttribute Otp otp) {
        if (transactionService.verify(refNo, otp))
            return "payment-successful";
        else
            return "payment-failed";
    }

    @GetMapping("/{refNo}/resend")
    public void resend(@PathVariable Integer refNo) {
        transactionService.resendOtp(refNo);
    }
}
