package com.example.accountservice.controller;

import com.example.accountservice.model.Account;
import com.example.accountservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    public Page<Account> findAll(@RequestParam int page, @RequestParam int size, @RequestParam(required = false) Long accHolderId) {
        if (accHolderId != null) {
            return accountService.findByAccountHolderId(page, size, accHolderId);
        }

        return accountService.findAll(page, size);
    }

    @GetMapping("/{id}")
    public Account findById(@PathVariable Long id) {
        return accountService.findById(id);
    }

    @PostMapping
    public Account save(@RequestBody Account account) {
        return accountService.save(account);
    }

    @PutMapping
    public Account update(@RequestBody Account account) {
        return accountService.update(account);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        accountService.deleteById(id);
    }
}
