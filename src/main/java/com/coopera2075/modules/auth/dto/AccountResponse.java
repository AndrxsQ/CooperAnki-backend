package com.coopera2075.modules.auth.dto;

public record AccountResponse(
        Long id,
        String fullName,
        String email,
        String role,
        boolean active,
        boolean emailNotificationsEnabled) {
}
