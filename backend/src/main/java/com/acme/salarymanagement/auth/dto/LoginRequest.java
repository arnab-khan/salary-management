package com.acme.salarymanagement.auth.dto;

public record LoginRequest(
        String username,
        String password
) {
}
