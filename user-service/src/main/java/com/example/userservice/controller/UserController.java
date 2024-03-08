package com.example.userservice.controller;

import com.example.userservice.dto.PasswordResetRequest;
import com.example.userservice.dto.UserRequest;
import com.example.userservice.dto.UserResponse;
import com.example.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findUser(id));
    }

    @GetMapping("/{id}/disable")
    public void disableUser(@PathVariable Long id) {
        userService.disableUser(id);
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.createUser(request));
    }

    @PutMapping("/{id}/reset")
    public void updateUser(@PathVariable Long id, @RequestBody PasswordResetRequest user) {
        userService.updateUser(id, user);
    }
}
