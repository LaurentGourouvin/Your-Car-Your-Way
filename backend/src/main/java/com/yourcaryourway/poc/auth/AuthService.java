package com.yourcaryourway.poc.auth;

import com.yourcaryourway.poc.auth.dto.AuthResponse;
import com.yourcaryourway.poc.auth.dto.RegisterRequest;
import com.yourcaryourway.poc.model.Role;
import com.yourcaryourway.poc.model.User;
import com.yourcaryourway.poc.repository.RoleRepository;
import com.yourcaryourway.poc.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;

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
