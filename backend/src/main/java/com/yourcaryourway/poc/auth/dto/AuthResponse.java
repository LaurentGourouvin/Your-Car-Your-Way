package com.yourcaryourway.poc.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

/**
 * DTO representing the response body after successful authentication.
 */
@Getter
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private UUID userId;
    private String email;
    private List<String> roles;
}