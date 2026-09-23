package com.shaileshmishra.app.auth.service;

import java.time.Instant;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shaileshmishra.app.auth.dto.AuthResponseDTO;
import com.shaileshmishra.app.auth.dto.LoginRequestDTO;
import com.shaileshmishra.app.auth.dto.RegisterRequestDTO;
import com.shaileshmishra.app.common.util.UtcTimestamp;
import com.shaileshmishra.app.exception.UserAlreadyExistsException;
import com.shaileshmishra.app.security.JwtTokenProvider;
import com.shaileshmishra.app.user.model.User;
import com.shaileshmishra.app.user.repository.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public AuthService(UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    public Map<String, Object> registerUser(RegisterRequestDTO registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new UserAlreadyExistsException("Username is already taken.");
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new UserAlreadyExistsException("Email is already in use.");
        }

        Set<String> roles = registerRequest.getRoles();
        if (roles == null || roles.isEmpty()) {
            roles = new HashSet<>();
            roles.add("USER");
        }

        Instant currentTimestamp = UtcTimestamp.nowAsInstant();
        User user = new User(
                registerRequest.getUsername(),
                registerRequest.getEmail(),
                passwordEncoder.encode(registerRequest.getPassword()),
                roles,
                currentTimestamp,
                currentTimestamp);

        userRepository.save(user);

        return Map.of("message", "User registered successfully", "username", user.getUsername());
    }

    public AuthResponseDTO loginUser(LoginRequestDTO loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);

        org.springframework.security.core.userdetails.UserDetails userDetails = 
                (org.springframework.security.core.userdetails.UserDetails) authentication.getPrincipal();

        Set<String> roles = userDetails.getAuthorities().stream()
                .map(auth -> auth.getAuthority().replace("ROLE_", ""))
                .collect(java.util.stream.Collectors.toSet());

        return new AuthResponseDTO(
                jwt,
                userDetails.getUsername(),
                roles,
                tokenProvider.getJwtExpirationMs());
    }
}
