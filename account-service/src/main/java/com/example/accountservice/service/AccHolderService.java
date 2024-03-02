package com.example.accountservice.service;

import com.example.accountservice.model.AccHolder;

public interface AccHolderService {

    AccHolder findById(int id);

    AccHolder save(AccHolder accHolder);

    AccHolder update(AccHolder accHolder);

    void deleteById(int id);
}
