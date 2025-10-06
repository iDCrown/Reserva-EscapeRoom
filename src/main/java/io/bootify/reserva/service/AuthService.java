package io.bootify.reserva.service;

import org.springframework.stereotype.Service;

import io.bootify.reserva.Auth.AuthResponse;
import io.bootify.reserva.Auth.LoginRequest;
import io.bootify.reserva.repos.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    public AuthResponse login(LoginRequest request) {
        return null;
    }

}
