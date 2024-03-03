package com.example.transactionservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transaction_confirmations")
public class TransactionConfirmation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "transactions_ref_no", nullable = false)
    private Transaction transaction;

    @NotNull
    @Column(name = "receiver_id", nullable = false)
    private Integer receiverId;

    @NotNull
    @Column(name = "expiration_time", nullable = false)
    private Instant expirationTime;

    @Size(max = 6, min = 6)
    @NotNull
    @Column(name = "otp", nullable = false, length = 6)
    private String otp;
}