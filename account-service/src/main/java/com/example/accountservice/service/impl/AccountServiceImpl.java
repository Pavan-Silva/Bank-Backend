package com.example.accountservice.service.impl;

import com.example.accountservice.model.Account;
import com.example.accountservice.repository.AccountRepository;
import com.example.accountservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Override
    public Account findById(int id) {
        return accountRepository.findById(id).orElseThrow();
    }

    @Override
    public Account save(Account account) {
        return accountRepository.save(account);
    }

    @Override
    public Account update(Account account) {
        accountRepository.findById(account.getId()).orElseThrow();
        return accountRepository.save(account);
    }

    @Override
    public void deleteById(int id) {
        accountRepository.findById(id).orElseThrow();
        accountRepository.deleteById(id);
    }
}
