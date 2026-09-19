package com.shaileshmishra.app.auth.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.shaileshmishra.app.auth.dto.AuthResponseDTO;
import com.shaileshmishra.app.auth.dto.LoginRequestDTO;
import com.shaileshmishra.app.auth.dto.RegisterRequestDTO;
import com.shaileshmishra.app.auth.service.AuthService;

import com.shaileshmishra.app.security.CustomUserDetailsService;
import com.shaileshmishra.app.security.JwtTokenProvider;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("AuthController")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @Test
    @DisplayName("should register user successfully")
    void shouldRegisterUser() throws Exception {
        when(authService.registerUser(any(RegisterRequestDTO.class)))
                .thenReturn(Map.of("message", "User registered successfully", "username", "testuser"));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "testuser",
                                    "email": "test@example.com",
                                    "password": "password123"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message", is("User registered successfully")))
                .andExpect(jsonPath("$.username", is("testuser")));
    }

    @Test
    @DisplayName("should login user successfully and return JWT token")
    void shouldLoginUser() throws Exception {
        AuthResponseDTO authResponse = new AuthResponseDTO("mocked-token", "testuser", Set.of("USER"), 86400000L);

        when(authService.loginUser(any(LoginRequestDTO.class))).thenReturn(authResponse);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "testuser",
                                    "password": "password123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", is("mocked-token")))
                .andExpect(jsonPath("$.tokenType", is("Bearer")))
                .andExpect(jsonPath("$.username", is("testuser")));
    }

    @Test
    @DisplayName("should return 400 when registration request is invalid")
    void shouldReturn400_whenRegistrationInvalid() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "",
                                    "email": "invalid-email",
                                    "password": "123"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }
}
