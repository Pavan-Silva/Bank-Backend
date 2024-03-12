package com.example.accountservice.service;

import com.example.accountservice.model.AccHolder;
import org.springframework.data.domain.Page;

public interface AccHolderService {

    Page<AccHolder> findAll(int page, int size);

    AccHolder findById(Long id);

    AccHolder save(AccHolder accHolder);

    AccHolder update(AccHolder accHolder);

    void deleteById(Long id);
}
