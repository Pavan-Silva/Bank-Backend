package com.example.authservice.service;

import com.example.authservice.dto.AuthRequest;
import com.example.authservice.dto.AuthResponse;

public interface AuthService {

    AuthResponse login(AuthRequest req);

    AuthResponse register(AuthRequest req);

    AuthResponse refresh(String token);
}
