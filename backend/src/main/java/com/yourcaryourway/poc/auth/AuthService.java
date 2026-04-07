package com.yourcaryourway.poc.auth;

import com.yourcaryourway.poc.auth.dto.AuthResponse;
import com.yourcaryourway.poc.auth.dto.LoginRequest;
import com.yourcaryourway.poc.auth.dto.RegisterRequest;
import com.yourcaryourway.poc.model.Role;
import com.yourcaryourway.poc.model.User;
import com.yourcaryourway.poc.repository.RoleRepository;
import com.yourcaryourway.poc.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;

    /**
     * Registers a new user with the CLIENT role.
     * Checks if the email is already in use before creating the account.
     *
     * @param request the registration request containing user details
     * @return an {@link AuthResponse} containing the JWT token and user information
     * @throws RuntimeException if the email is already in use
     * @throws RuntimeException if the CLIENT role is not found in the database
     */
    public AuthResponse register(RegisterRequest request) {
        Optional<User> findUserByEmail = userRepository.findByEmail(request.getEmail());

        if (findUserByEmail.isPresent()) {
            throw new RuntimeException("Email already in use");
        }

        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .firstname(request.getFirstName())
                .lastname(request.getLastName())
                .address(request.getAddress())
                .birthdate(request.getBirthDate())
                .roles(new HashSet<>())
                .build();

        Role clientRole = roleRepository.findByName("CLIENT")
                .orElseThrow(() -> new RuntimeException("Role CLIENT not found"));

        user.getRoles().add(clientRole);

        userRepository.save(user);

        List<String> roles = getRoles(user);

        String token = generateToken(user);

        return new AuthResponse(token, user.getId(), user.getEmail(), roles);
    }

    /**
     * Authenticates a user with their email and password.
     * Returns a JWT token if the credentials are valid.
     *
     * @param request the login request containing email and password
     * @return an {@link AuthResponse} containing the JWT token and user information
     * @throws RuntimeException if the email is not found or the password is incorrect
     */
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Incorrect email or credentials"));

        if(!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Incorrect email or credentials");
        }

        List<String> roles = getRoles(user);

        String token = generateToken(user);

        return new AuthResponse(token, user.getId(), user.getEmail(), roles);
    }

    /**
     * Extracts the list of role names from a user.
     *
     * @param user the user whose roles are to be extracted
     * @return a list of role names (e.g. ["CLIENT", "SUPPORT"])
     */
    private List<String> getRoles(User user) {
        return user.getRoles().stream().map(Role::getName).toList();
    }

    /**
     * Generates a JWT token for the given user.
     * Delegates to JwtService#generateToken.
     *
     * @param user the authenticated user
     * @return the signed JWT token as a string
     */
    private String generateToken(User user) {
        return jwtService.generateToken(user.getEmail(), user.getId(), getRoles(user));
    }
}
