package com.example.userservice.model;

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
@Table(name = "user_verifications")
public class UserVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 45)
    @NotNull
    @Column(name = "user_id", nullable = false, length = 45)
    private String userId;

    @Size(max = 6)
    @NotNull
    @Column(name = "code", nullable = false, length = 6)
    private String code;

    @NotNull
    @Column(name = "expiration_time", nullable = false)
    private Instant expirationTime;
}