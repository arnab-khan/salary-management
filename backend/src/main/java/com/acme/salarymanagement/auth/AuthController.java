package com.acme.salarymanagement.auth;

import com.acme.salarymanagement.auth.dto.AuthResponse;
import com.acme.salarymanagement.auth.dto.LoginRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // API URL: POST http://localhost:8080/api/auth/login
    // Body: { "username": "hr@acme.com", "password": "admin123" }
    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        return authService.login(loginRequest, request);
    }

    // API URL: GET http://localhost:8080/api/auth/me
    @GetMapping("/me")
    public AuthResponse me(Authentication authentication) {
        return authService.me(authentication);
    }

    // API URL: POST http://localhost:8080/api/auth/logout
    @PostMapping("/logout")
    public void logout(HttpServletRequest request) {
        authService.logout(request);
    }
}
