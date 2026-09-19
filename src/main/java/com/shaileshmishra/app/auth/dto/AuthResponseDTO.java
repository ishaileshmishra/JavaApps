package com.shaileshmishra.app.auth.dto;

import java.util.Set;

public class AuthResponseDTO {

    private String token;
    private String tokenType = "Bearer";
    private String username;
    private Set<String> roles;
    private long expiresInMs;

    public AuthResponseDTO() {
    }

    public AuthResponseDTO(String token, String username, Set<String> roles, long expiresInMs) {
        this.token = token;
        this.username = username;
        this.roles = roles;
        this.expiresInMs = expiresInMs;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public long getExpiresInMs() {
        return expiresInMs;
    }

    public void setExpiresInMs(long expiresInMs) {
        this.expiresInMs = expiresInMs;
    }
}

