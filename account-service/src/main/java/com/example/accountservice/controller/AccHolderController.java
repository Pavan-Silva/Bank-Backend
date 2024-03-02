package com.example.accountservice.controller;

import com.example.accountservice.model.AccHolder;
import com.example.accountservice.service.AccHolderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/acc_holders")
public class AccHolderController {

    private final AccHolderService accHolderService;

    @GetMapping("/{id}")
    public AccHolder find(@PathVariable int id) {
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
    public void delete(@PathVariable int id) {
        accHolderService.deleteById(id);
    }
}
