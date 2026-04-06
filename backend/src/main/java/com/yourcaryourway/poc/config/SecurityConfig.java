package com.yourcaryourway.poc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Spring Security configuration.
 * Defines the security beans used across the application.
 */
@Configuration
public class SecurityConfig {

    /**
     * Declares the BCrypt password encoder as a Spring bean.
     * Used to hash and verify passwords.
     *
     * @return a BCryptPasswordEncoder with strength 12
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}