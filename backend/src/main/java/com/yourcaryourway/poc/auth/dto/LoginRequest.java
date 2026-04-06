package com.yourcaryourway.poc.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO representing the request body for user login.
 */
@Getter
@Setter
@NoArgsConstructor
public class LoginRequest {

    private String email;
    private String password;
}