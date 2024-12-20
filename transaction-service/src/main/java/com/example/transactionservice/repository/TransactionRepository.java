package com.example.transactionservice.repository;

import com.example.transactionservice.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByRefNo(Long refNo);

    Page<Transaction> findByMainAccountId(Pageable pageable, Long accId);
}