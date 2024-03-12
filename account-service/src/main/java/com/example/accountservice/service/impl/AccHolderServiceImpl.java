package com.example.accountservice.service.impl;

import com.example.accountservice.exception.BadRequestException;
import com.example.accountservice.exception.NotFoundException;
import com.example.accountservice.model.AccHolder;
import com.example.accountservice.repository.AccHolderRepository;
import com.example.accountservice.service.AccHolderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccHolderServiceImpl implements AccHolderService {

    private final AccHolderRepository accHolderRepository;

    @Override
    public Page<AccHolder> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.ASC, "id");
        return accHolderRepository.findAll(pageable);
    }

    @Override
    public AccHolder findById(Long id) {
        return accHolderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Couldn't find account holder"));
    }

    @Override
    public AccHolder save(AccHolder accHolder) {
        boolean existsByNic = accHolderRepository.existsByNic(accHolder.getNic());
        boolean existsByEmail = accHolderRepository.existsByEmail(accHolder.getEmail());
        boolean existsByMobile = accHolderRepository.existsByMobile(accHolder.getMobile());

        if (!existsByEmail && !existsByNic && !existsByMobile)
            return accHolderRepository.save(accHolder);
        else throw new BadRequestException("Account holder already exists");
    }

    @Override
    public AccHolder update(AccHolder accHolder) {
        AccHolder existingAccHolder = findById(accHolder.getId());

        if (accHolder.getMobile() == null)
            accHolder.setMobile(existingAccHolder.getMobile());

        if (accHolder.getNic() == null)
            accHolder.setNic(existingAccHolder.getNic());

        if (accHolder.getEmail() == null)
            accHolder.setEmail(existingAccHolder.getEmail());

        if (accHolder.getAddress() == null)
            accHolder.setAddress(existingAccHolder.getAddress());

        if (accHolder.getName() == null)
            accHolder.setName(existingAccHolder.getName());

        if (accHolder.getGender() == null)
            accHolder.setGender(existingAccHolder.getGender());

        return accHolderRepository.save(accHolder);
    }

    @Override
    public void deleteById(Long id) {
        findById(id);
        accHolderRepository.deleteById(id);
    }
}
