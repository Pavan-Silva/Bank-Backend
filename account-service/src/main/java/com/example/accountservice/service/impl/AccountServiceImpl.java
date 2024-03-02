package com.example.accountservice.service.impl;

import com.example.accountservice.model.*;
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
        Account existingAcc = accountRepository.findById(account.getId()).orElseThrow();

        if (account.getAccHolder() == null) {
            AccHolder accHolder = AccHolder.builder()
                    .id(existingAcc.getAccHolder().getId())
                    .build();

            account.setAccHolder(accHolder);
        }

        if (account.getBranch() == null) {
            Branch branch = Branch.builder()
                    .id(existingAcc.getBranch().getId())
                    .build();

            account.setBranch(branch);
        }

        if (account.getAccStatus() == null) {
            AccStatus accStatus = AccStatus.builder()
                    .id(existingAcc.getAccStatus().getId())
                    .build();

            account.setAccStatus(accStatus);
        }

        if (account.getAccType() == null) {
            AccType accType = AccType.builder()
                    .id(existingAcc.getAccType().getId())
                    .build();

            account.setAccType(accType);
        }

        if (account.getDateCreated() == null)
            account.setDateCreated(existingAcc.getDateCreated());

        if (account.getCurrentBalance() == null)
            account.setCurrentBalance(existingAcc.getCurrentBalance());

        return accountRepository.save(account);
    }

    @Override
    public void deleteById(int id) {
        accountRepository.findById(id).orElseThrow();
        accountRepository.deleteById(id);
    }
}
