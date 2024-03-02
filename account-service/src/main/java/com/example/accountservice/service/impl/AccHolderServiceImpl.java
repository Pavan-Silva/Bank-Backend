package com.example.accountservice.service.impl;

import com.example.accountservice.model.AccHolder;
import com.example.accountservice.repository.AccHolderRepository;
import com.example.accountservice.service.AccHolderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccHolderServiceImpl implements AccHolderService {

    private final AccHolderRepository accHolderRepository;

    @Override
    public AccHolder findById(int id) {
        return accHolderRepository.findById(id).orElseThrow();
    }

    @Override
    public AccHolder save(AccHolder accHolder) {
        return accHolderRepository.save(accHolder);
    }

    @Override
    public AccHolder update(AccHolder accHolder) {
        return accHolderRepository.save(accHolder);
    }

    @Override
    public void deleteById(int id) {
        accHolderRepository.deleteById(id);
    }
}
