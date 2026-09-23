package com.shaileshmishra.app.auth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.shaileshmishra.app.auth.dto.AuthResponseDTO;
import com.shaileshmishra.app.auth.dto.LoginRequestDTO;
import com.shaileshmishra.app.auth.dto.RegisterRequestDTO;
import com.shaileshmishra.app.exception.UserAlreadyExistsException;
import com.shaileshmishra.app.security.JwtTokenProvider;
import com.shaileshmishra.app.user.model.User;
import com.shaileshmishra.app.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    @InjectMocks
    private AuthService authService;

    private RegisterRequestDTO registerRequest;
    private LoginRequestDTO loginRequest;
    private User user;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequestDTO("testuser", "test@example.com", "password123");
        loginRequest = new LoginRequestDTO("testuser", "password123");
        user = new User("1", "testuser", "test@example.com", "encodedPassword", Set.of("USER"), Instant.now(), Instant.now());
    }

    @Test
    @DisplayName("should register user successfully when username and email are available")
    void shouldRegisterUser_whenAvailable() {
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        Map<String, Object> result = authService.registerUser(registerRequest);

        assertNotNull(result);
        assertEquals("User registered successfully", result.get("message"));
        assertEquals("testuser", result.get("username"));
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("should throw exception when username already exists")
    void shouldThrowException_whenUsernameExists() {
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        UserAlreadyExistsException ex = assertThrows(UserAlreadyExistsException.class,
                () -> authService.registerUser(registerRequest));

        assertEquals("Username is already taken.", ex.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("should login user successfully and return JWT token")
    void shouldLoginUser_whenCredentialsAreValid() {
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(tokenProvider.generateToken(authentication)).thenReturn("mocked-jwt-token");
        when(tokenProvider.getJwtExpirationMs()).thenReturn(86400000L);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        AuthResponseDTO response = authService.loginUser(loginRequest);

        assertNotNull(response);
        assertEquals("mocked-jwt-token", response.getToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals("testuser", response.getUsername());
        assertEquals(86400000L, response.getExpiresInMs());
    }
}

