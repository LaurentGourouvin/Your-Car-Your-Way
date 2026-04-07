package com.yourcaryourway.poc.auth;

import com.yourcaryourway.poc.auth.dto.AuthResponse;
import com.yourcaryourway.poc.auth.dto.LoginRequest;
import com.yourcaryourway.poc.auth.dto.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    /**
     * Registers a new user account.
     *
     * @param request the registration request containing user details
     * @return a 201 Created response with the JWT token and user information
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(201).body(response);
    }

    /**
     * Authenticates a user with their email and password.
     *
     * @param request the login request containing email and password
     * @return a 200 OK response with the JWT token and user information
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

}
