package com.example.userservice.controller;

import com.example.userservice.dto.UserRequest;
import com.example.userservice.dto.UserResponse;
import com.example.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.createUser(request));
    }

//    @PutMapping("/update/{id}")
//    public ResponseEntity<?> updateUser(@PathVariable("id") Long userId, @RequestBody User user) {
//        return ResponseEntity.ok(userService.updateUser(userId, user));
//    }
//
//    @GetMapping
//    public ResponseEntity<?> readUsers() {
//        return ResponseEntity.ok(userService.readUsers());
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<?> readUser(@PathVariable("id") Long id) {
//        return ResponseEntity.ok(userService.readUser(id));
//    }
}
