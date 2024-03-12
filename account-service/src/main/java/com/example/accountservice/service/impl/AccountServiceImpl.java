package com.example.accountservice.service.impl;

import com.example.accountservice.exception.NotFoundException;
import com.example.accountservice.model.*;
import com.example.accountservice.repository.AccountRepository;
import com.example.accountservice.service.AccHolderService;
import com.example.accountservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccHolderService accHolderService;

    @Override
    public Account findById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No any accounts associated with provided info"));
    }

    @Override
    public Account save(Account account) {
        accHolderService.findById(account.getAccHolder().getId());
        return accountRepository.save(account);
    }

    @Override
    public Account update(Account account) {
        Account existingAcc = findById(account.getId());

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

        if (account.getFailedTransactionAttempts() == null)
            account.setFailedTransactionAttempts(existingAcc.getFailedTransactionAttempts());

        return accountRepository.save(account);
    }

    @Override
    public void deleteById(Long id) {
        findById(id);
        accountRepository.deleteById(id);
    }
}
