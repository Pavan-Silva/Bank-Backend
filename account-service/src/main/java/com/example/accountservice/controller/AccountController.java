package com.example.accountservice.controller;

import com.example.accountservice.model.Account;
import com.example.accountservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public Account find(@PathVariable int id) {
        return accountService.findById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('STAFF_MEMBER')")
    public Account save(@RequestBody Account account) {
        return accountService.save(account);
    }

    @PutMapping
    @PreAuthorize("hasRole('STAFF_MEMBER')")
    public Account update(@RequestBody Account account) {
        return accountService.update(account);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('STAFF_MEMBER')")
    public void delete(@PathVariable int id) {
        accountService.deleteById(id);
    }
}
