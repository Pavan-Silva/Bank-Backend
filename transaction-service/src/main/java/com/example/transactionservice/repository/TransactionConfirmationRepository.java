package com.example.transactionservice.repository;

import com.example.transactionservice.model.Transaction;
import com.example.transactionservice.model.TransactionConfirmation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransactionConfirmationRepository extends JpaRepository<TransactionConfirmation, Integer> {

    Optional<TransactionConfirmation> findByTransaction_RefNo(Integer refNo);

    void deleteByTransaction(Transaction transaction);
}