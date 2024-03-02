package com.example.accountservice.service;

import com.example.accountservice.model.Account;

public interface AccountService {

    Account findById(int id);

    Account save(Account account);

    Account update(Account account);

    void deleteById(int id);
}
