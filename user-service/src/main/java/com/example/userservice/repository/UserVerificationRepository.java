package com.example.userservice.repository;

import com.example.userservice.model.UserVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserVerificationRepository extends JpaRepository<UserVerification, Integer> {

    Optional<UserVerification> findByUserId(String userId);
    void deleteAllByUserId(String userId);
}