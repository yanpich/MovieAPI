package com.movieflix.movieapi.service;

import com.movieflix.movieapi.auth.entities.User;
import com.movieflix.movieapi.auth.entities.UserRole;
import com.movieflix.movieapi.auth.repositories.UserRepository;
import com.movieflix.movieapi.auth.services.JwtService;
import com.movieflix.movieapi.auth.services.RefreshTokenService;
import com.movieflix.movieapi.auth.utils.AuthResponse;
import com.movieflix.movieapi.auth.utils.LoginRequest;
import com.movieflix.movieapi.auth.utils.RegisterRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest registerRequest) {
        // Check if email already exists
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered: " + registerRequest.getEmail());
        }

        // Create username from email if not provided
        String username = registerRequest.getUsername();
        if (username == null || username.trim().isEmpty()) {
            username = registerRequest.getEmail().split("@")[0];
            // Ensure username is unique
            String baseUsername = username;
            int counter = 1;
            while (userRepository.findByUsername(username).isPresent()) {
                username = baseUsername + counter;
                counter++;
            }
        }

        // Build user entity with ALL fields explicitly set
        var user = User.builder()
                .name(registerRequest.getName())
                .email(registerRequest.getEmail().toLowerCase().trim())
                .username(username)
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(UserRole.USER)
                .isAccountNonExpired(true)
                .isAccountNonLocked(true)
                .isCredentialsNonExpired(true)
                .isEnabled(true)
                .build();

        User savedUser = userRepository.save(user);
        log.info("User registered successfully: {}", savedUser.getEmail());

        var accessToken = jwtService.generateToken(savedUser);
        var refreshToken = refreshTokenService.createRefreshToken(savedUser.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getRefreshToken())
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .build();
    }

    public AuthResponse login(LoginRequest loginRequest) {
        try {
            String email = loginRequest.getEmail().toLowerCase().trim();

            log.info("Attempting login for email: {}", email);

            // Authenticate using email
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            email,  // This will be used by CustomUserDetailsService
                            loginRequest.getPassword()
                    )
            );

            // IMPORTANT: Find user by EMAIL, not username
            var user = userRepository
                    .findByEmail(email)  // Changed from findByUsername to findByEmail
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

            var accessToken = jwtService.generateToken(user);
            var refreshToken = refreshTokenService.createRefreshToken(user.getEmail());

            log.info("User logged in successfully: {}", user.getEmail());

            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken.getRefreshToken())
                    .name(user.getName())
                    .email(user.getEmail())
                    .build();

        } catch (BadCredentialsException e) {
            log.error("Invalid password for email: {}", loginRequest.getEmail());
            throw new BadCredentialsException("Invalid email or password");
        } catch (Exception e) {
            log.error("Login error: {}", e.getMessage());
            throw new RuntimeException("Login failed: " + e.getMessage());
        }
    }
}