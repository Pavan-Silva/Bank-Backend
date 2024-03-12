package com.example.transactionservice.repository;

import com.example.transactionservice.model.PendingOnlineTransaction;
import com.example.transactionservice.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PendingTransactionRepository extends JpaRepository<PendingOnlineTransaction, Long> {

    Optional<PendingOnlineTransaction> findByTransaction_RefNo(Long refNo);

    void deleteByTransaction(Transaction transaction);
}