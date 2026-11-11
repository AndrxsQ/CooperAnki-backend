package com.coopera2075.cooperanki.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AuthResponse(
        String accessToken,
        String tokenType,
        Long id,
        String name,
        String username,
        String email,
        String role,
        String accountSystem,
        List<String> allowedSystems,
        String refreshToken,
        String defaultSystem) {
}
