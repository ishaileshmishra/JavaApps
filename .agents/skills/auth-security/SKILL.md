---
name: auth-security
description: >-
  Use this skill when modifying or debugging authentication, JWT token generation/validation, Spring Security filter chains, password hashing, or the user persistence model.
---

# Auth & Security Skill

This skill provides architecture and operational rules for `com.shaileshmishra.app.auth`, `com.shaileshmishra.app.security`, and `com.shaileshmishra.app.user`.

---

## Package Structure

### 1. Security (`com.shaileshmishra.app.security`)
- [`SecurityConfig`](file:///Users/shaileshmishra/Documents/codespace/JavaApps/src/main/java/com/shaileshmishra/app/security/SecurityConfig.java): Configures stateless HTTP security, permits, and exception handlers.
- [`JwtAuthenticationFilter`](file:///Users/shaileshmishra/Documents/codespace/JavaApps/src/main/java/com/shaileshmishra/app/security/JwtAuthenticationFilter.java): Extracts and parses `Bearer <token>` on incoming requests.
- [`JwtTokenProvider`](file:///Users/shaileshmishra/Documents/codespace/JavaApps/src/main/java/com/shaileshmishra/app/security/JwtTokenProvider.java): HMAC-SHA secret key generation, token signing, and claim validation.
- [`JwtAuthenticationEntryPoint`](file:///Users/shaileshmishra/Documents/codespace/JavaApps/src/main/java/com/shaileshmishra/app/security/JwtAuthenticationEntryPoint.java): Emits 401 Unauthorized for unauthenticated requests.
- [`CustomUserDetailsService`](file:///Users/shaileshmishra/Documents/codespace/JavaApps/src/main/java/com/shaileshmishra/app/security/CustomUserDetailsService.java): Bridges Spring Security `UserDetails` with MongoDB `User` entity.

### 2. Authentication (`com.shaileshmishra.app.auth`)
- [`AuthController`](file:///Users/shaileshmishra/Documents/codespace/JavaApps/src/main/java/com/shaileshmishra/app/auth/controller/AuthController.java): Exposes `/auth/register` and `/auth/login`.
- [`AuthService`](file:///Users/shaileshmishra/Documents/codespace/JavaApps/src/main/java/com/shaileshmishra/app/auth/service/AuthService.java): Coordinates authentication via `AuthenticationManager` and user registration.
- DTOs: [`RegisterRequestDTO`](file:///Users/shaileshmishra/Documents/codespace/JavaApps/src/main/java/com/shaileshmishra/app/auth/dto/RegisterRequestDTO.java), [`LoginRequestDTO`](file:///Users/shaileshmishra/Documents/codespace/JavaApps/src/main/java/com/shaileshmishra/app/auth/dto/LoginRequestDTO.java), [`AuthResponseDTO`](file:///Users/shaileshmishra/Documents/codespace/JavaApps/src/main/java/com/shaileshmishra/app/auth/dto/AuthResponseDTO.java).

### 3. User Persistence (`com.shaileshmishra.app.user`)
- [`User`](file:///Users/shaileshmishra/Documents/codespace/JavaApps/src/main/java/com/shaileshmishra/app/user/model/User.java): MongoDB document (`@Document(collection = "users")`).
- [`UserRepository`](file:///Users/shaileshmishra/Documents/codespace/JavaApps/src/main/java/com/shaileshmishra/app/user/repository/UserRepository.java): Query interface exposing `findByUsername(String username)`.

---

## Security Invariants & Workflows

### 1. Endpoint Access Rules
- **Public**: `/auth/**` and `/error` require no credentials (`permitAll()`).
- **Protected**: All other routes (e.g. `/employees/**`) require a valid JWT header (`Authorization: Bearer <jwt-token>`).

### 2. Password Security
- Never store plaintext passwords. All user passwords are encrypted using `BCryptPasswordEncoder`.

### 3. JWT Token Lifecycle
- Configured in `application.properties`:
  - `app.jwt.secret`: 256-bit hexadecimal string decoded into HMAC-SHA `SecretKey`.
  - `app.jwt.expiration-ms`: 86400000 ms (24 hours).
- Login generates token using:
  ```java
  Authentication authentication = authenticationManager.authenticate(
      new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
  );
  String token = jwtTokenProvider.generateToken(authentication);
  ```

---

## Testing & Verification

- Unit test JWT signing and expiry using [`JwtTokenProviderTest`](file:///Users/shaileshmishra/Documents/codespace/JavaApps/src/test/java/com/shaileshmishra/app/security/JwtTokenProviderTest.java).
- Verify 401/403 behaviors using [`SecurityIntegrationTest`](file:///Users/shaileshmishra/Documents/codespace/JavaApps/src/test/java/com/shaileshmishra/app/security/SecurityIntegrationTest.java).
