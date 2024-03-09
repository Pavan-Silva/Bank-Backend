package com.example.accountservice.controller;

import com.example.accountservice.model.AccHolder;
import com.example.accountservice.service.AccHolderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/acc-holders")
public class AccHolderController {

    private final AccHolderService accHolderService;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public AccHolder find(@PathVariable int id) {
        return accHolderService.findById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('STAFF_MEMBER')")
    public AccHolder save(@RequestBody AccHolder accHolder) {
        return accHolderService.save(accHolder);
    }

    @PutMapping
    @PreAuthorize("hasRole('STAFF_MEMBER')")
    public AccHolder update(@RequestBody AccHolder accHolder) {
        return accHolderService.update(accHolder);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('STAFF_MEMBER')")
    public void delete(@PathVariable int id) {
        accHolderService.deleteById(id);
    }
}
