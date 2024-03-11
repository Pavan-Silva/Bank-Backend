package com.example.accountservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Builder
@Entity
@Table(name = "accounts")
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "current_balance", precision = 12, scale = 2)
    private BigDecimal currentBalance;

    @Column(name = "date_created")
    private Instant dateCreated;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "acc_holder_id", nullable = false)
    private AccHolder accHolder;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "acc_type_id", nullable = false)
    private AccType accType;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "acc_status_id", nullable = false)
    private AccStatus accStatus;

    @Column(name = "failed_transaction_attempts")
    private Integer failedTransactionAttempts;
}