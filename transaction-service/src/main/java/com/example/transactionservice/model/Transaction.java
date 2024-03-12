package com.example.transactionservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transactions")
public class Transaction {

    @Id
    @Column(name = "ref_no", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer refNo;

    @NotNull
    @Column(name = "date", nullable = false)
    private Instant date;

    @NotNull
    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @NotNull
    @Column(name = "account_id", nullable = false)
    private Integer accountId;

    @NotNull
    @Column(name = "acc_balance", nullable = false)
    private BigDecimal accBalance;

    @Size(max = 100)
    @Column(name = "description", length = 100)
    private String description;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "transaction_status_id", nullable = false)
    private TransactionStatus transactionStatus;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "transaction_type_id", nullable = false)
    private TransactionType transactionType;
}