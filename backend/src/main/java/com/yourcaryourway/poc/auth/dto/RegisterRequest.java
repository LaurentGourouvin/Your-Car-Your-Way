package com.yourcaryourway.poc.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * DTO representing the request body for user registration.
 */
@Getter
@Setter
@NoArgsConstructor
public class RegisterRequest {

    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String address;
}