package com.example.authservice.service.impl;

import com.example.authservice.dto.AuthRequest;
import com.example.authservice.dto.AuthResponse;
import com.example.authservice.exception.AuthenticationException;
import com.example.authservice.model.User;
import com.example.authservice.repository.UserRepository;
import com.example.authservice.service.AuthService;
import com.example.authservice.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthResponse login(AuthRequest req) {
        User user = userRepository.findByUsername(req.getUsername())
                .orElseThrow(() -> new AuthenticationException("Invalid credentials"));

        if (passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            return AuthResponse.builder()
                    .accessToken(jwtUtil.generateAccessToken(req))
                    .refreshToken(jwtUtil.generateRefreshToken(req))
                    .accHolderId(user.getAccHolderId())
                    .build();
        }

        else throw new AuthenticationException("Invalid credentials");
    }

    @Override
    public AuthResponse register(AuthRequest req) {
        return null;
    }

    @Override
    public AuthResponse refresh(String token) {
        return null;
    }
}
