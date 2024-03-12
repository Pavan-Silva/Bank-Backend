package com.example.accountservice.service;

import com.example.accountservice.model.Account;

public interface AccountService {

    Account findById(Long id);

    Account save(Account account);

    Account update(Account account);

    void deleteById(Long id);
}
