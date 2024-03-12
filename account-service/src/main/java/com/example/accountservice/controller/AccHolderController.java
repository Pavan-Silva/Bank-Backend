package com.example.accountservice.controller;

import com.example.accountservice.model.AccHolder;
import com.example.accountservice.service.AccHolderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/acc-holders")
public class AccHolderController {

    private final AccHolderService accHolderService;

    @GetMapping
    public Page<AccHolder> findAll(@RequestParam int page, @RequestParam int size) {
        return accHolderService.findAll(page, size);
    }

    @GetMapping("/{id}")
    public AccHolder find(@PathVariable Long id) {
        return accHolderService.findById(id);
    }

    @PostMapping
    public AccHolder save(@RequestBody AccHolder accHolder) {
        return accHolderService.save(accHolder);
    }

    @PutMapping
    public AccHolder update(@RequestBody AccHolder accHolder) {
        return accHolderService.update(accHolder);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        accHolderService.deleteById(id);
    }
}
