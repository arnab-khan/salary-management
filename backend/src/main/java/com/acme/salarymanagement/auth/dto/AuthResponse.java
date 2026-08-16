package com.acme.salarymanagement.auth.dto;

public record AuthResponse(
        String username,
        boolean authenticated
) {
}
