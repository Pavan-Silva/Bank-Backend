package com.example.userservice.controller;

import com.example.userservice.dto.PasswordResetRequest;
import com.example.userservice.dto.UserRequest;
import com.example.userservice.dto.UserResponse;
import com.example.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('STAFF_MEMBER')")
    public ResponseEntity<UserResponse> findUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findUser(id));
    }

    @GetMapping("/{id}/disable")
    @PreAuthorize("hasRole('STAFF_MEMBER')")
    public void disableUser(@PathVariable Long id) {
        userService.disableUser(id);
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.createUser(request));
    }

    @PutMapping("/{id}/reset")
    @PreAuthorize("hasRole('USER')")
    public void updateUser(@PathVariable Long id, @RequestBody PasswordResetRequest user) {
        userService.updateUser(id, user);
    }
}
