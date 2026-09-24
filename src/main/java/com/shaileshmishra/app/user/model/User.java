package com.shaileshmishra.app.user.model;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {

    @Id
    private String id;

    @Indexed(unique = true)
    private String username;

    @Indexed(unique = true)
    private String email;

    private String password;

    @Builder.Default
    private Set<String> roles = new HashSet<>();

    private Instant createdAt;

    private Instant updatedAt;

    public User(String username, String email, String password, Set<String> roles, Instant createdAt,
            Instant updatedAt) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.roles = roles != null ? roles : new HashSet<>();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
