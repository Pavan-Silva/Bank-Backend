package com.example.accountservice.repository;

import com.example.accountservice.model.AccHolder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccHolderRepository extends JpaRepository<AccHolder, Integer> {

    boolean existsByNic(String nic);
    boolean existsByEmail(String email);
    boolean existsByMobile(String mobile);
}