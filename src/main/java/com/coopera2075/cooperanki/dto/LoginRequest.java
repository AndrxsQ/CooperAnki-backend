package com.coopera2075.cooperanki.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import com.coopera2075.modules.auth.LoginSystem;

public record LoginRequest(
        @NotBlank(message = "El correo es obligatorio")
        @Size(max = 255, message = "El correo no puede superar 255 caracteres")
        String username,
        @NotBlank(message = "La contraseña es obligatoria")
        @Size(max = 72, message = "La contraseña no puede superar 72 caracteres")
        String password,
        LoginSystem system) {
}
