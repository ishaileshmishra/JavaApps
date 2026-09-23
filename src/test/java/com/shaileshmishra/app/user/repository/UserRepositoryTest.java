package com.shaileshmishra.app.user.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.shaileshmishra.app.user.model.User;

@SpringBootTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("encoded_password")
                .roles(Set.of("ROLE_USER"))
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        userRepository.save(sampleUser);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("findByUsername should return user when exists")
    void findByUsername_shouldReturnUser() {
        Optional<User> result = userRepository.findByUsername("testuser");
        assertTrue(result.isPresent());
        assertEquals("test@example.com", result.get().getEmail());
    }

    @Test
    @DisplayName("findByUsername should return empty when user does not exist")
    void findByUsername_shouldReturnEmpty() {
        Optional<User> result = userRepository.findByUsername("nonexistent");
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("existsByUsername should return true when exists")
    void existsByUsername_shouldReturnTrue() {
        assertTrue(userRepository.existsByUsername("testuser"));
    }

    @Test
    @DisplayName("existsByUsername should return false when does not exist")
    void existsByUsername_shouldReturnFalse() {
        assertFalse(userRepository.existsByUsername("nonexistent"));
    }

    @Test
    @DisplayName("existsByEmail should return true when exists")
    void existsByEmail_shouldReturnTrue() {
        assertTrue(userRepository.existsByEmail("test@example.com"));
    }

    @Test
    @DisplayName("existsByEmail should return false when does not exist")
    void existsByEmail_shouldReturnFalse() {
        assertFalse(userRepository.existsByEmail("nonexistent@example.com"));
    }
}
