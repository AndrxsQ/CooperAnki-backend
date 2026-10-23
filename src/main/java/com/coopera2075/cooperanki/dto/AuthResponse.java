package com.coopera2075.cooperanki.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AuthResponse(
        String accessToken,
        String tokenType,
        Integer id,
        String name,
        String username,
        String email,
        String role) {
}
