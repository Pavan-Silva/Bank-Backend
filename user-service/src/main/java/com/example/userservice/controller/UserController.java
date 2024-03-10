package com.example.userservice.controller;

import com.example.userservice.dto.request.PasswordResetRequest;
import com.example.userservice.dto.request.UserRequest;
import com.example.userservice.dto.request.UserVerificationRequest;
import com.example.userservice.dto.response.UserResponse;
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
    public ResponseEntity<UserResponse> findUser(@PathVariable String id) {
        return ResponseEntity.ok(userService.findUser(id));
    }

    @GetMapping("/{id}/disable")
    public void disableUser(@PathVariable String id) {
        userService.disableUser(id);
    }

    @GetMapping("/{id}/verify")
    public void verifyUser(@PathVariable String id, @RequestBody UserVerificationRequest request) {
        userService.verifyUser(id, request);
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
