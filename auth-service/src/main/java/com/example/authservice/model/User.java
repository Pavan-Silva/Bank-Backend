package com.example.authservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 45)
    @NotNull
    @Column(name = "username", nullable = false, length = 45)
    private String username;

    @Size(max = 100)
    @NotNull
    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "acc_holder_id")
    private Integer accHolderId;

    @Column(name = "last_refresh")
    private Instant lastRefresh;

    @NotNull
    @Column(name = "failed_login_attempts", nullable = false)
    private Integer failedLoginAttempts;

    @NotNull
    @Column(name = "disabled", nullable = false)
    private Boolean disabled;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "roles_id", nullable = false)
    private Role roles;
}