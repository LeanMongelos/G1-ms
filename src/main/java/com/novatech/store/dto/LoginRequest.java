package com.novatech.store.dto;

import com.novatech.store.validation.ValidationPatterns;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank(message = "El email es obligatorio.")
        @Email(message = "El email no tiene un formato valido.")
        @Pattern(regexp = ValidationPatterns.EMAIL, message = "El email no tiene un formato valido.")
        String email,
        @NotBlank(message = "La contrasena es obligatoria.")
        @Size(min = 1, message = "La contrasena es obligatoria.")
        String contrasena) {
}
