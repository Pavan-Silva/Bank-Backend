package com.example.accountservice.service;

import com.example.accountservice.model.Account;
import org.springframework.data.domain.Page;

public interface AccountService {

    Page<Account> findAll(int page, int size);

    Page<Account> findByAccountHolderId(int page, int size, Long id);

    Account findById(Long id);

    Account save(Account account);

    Account update(Account account);

    void deleteById(Long id);
}
