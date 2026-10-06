package com.coopera2075.cooperanki.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequest {
    @NotBlank (message = "El nombre de usuario no puede estar vacío")
    @Size (min = 2, max = 50, message = "El nombre de usuario debe tener entre 2 y 50 caracteres")
    private String username;

    @NotBlank (message = "El correo electrónico no puede estar vacío")
    @Email (message = "El correo electrónico debe tener un formato válido")
    private String email;

    // Opcional: solo se envía si se quiere cambiar la contraseña
    @Size (min = 8, max = 72, message = "La contraseña debe tener entre 8 y 72 caracteres")
    private String password;

    // Obligatoria únicamente cuando se envía una nueva contraseña
    private String currentPassword;
}
