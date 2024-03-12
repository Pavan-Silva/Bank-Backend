package com.example.accountservice.repository;

import com.example.accountservice.model.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Page<Account> findByAccHolder_Id(Pageable pageable, Long id);
}