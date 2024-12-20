package com.example.userservice.service;

import com.example.userservice.dto.request.PasswordResetRequest;
import com.example.userservice.dto.request.UserRequest;
import com.example.userservice.dto.request.UserVerificationRequest;
import com.example.userservice.dto.response.UserResponse;

import java.util.List;

public interface UserService {

    List<UserResponse> findAll(int page, int size);

    UserResponse findUser(String userId);

    UserResponse createUser(UserRequest userRequest);

    void verifyUser(String userId, UserVerificationRequest request);

    void updateUser(Long userId, PasswordResetRequest req);

    void disableUser(String userId);
}
