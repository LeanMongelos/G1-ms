package com.novatech.store.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ActualizarPerfilRequest(
        @NotBlank String nombre,
        @NotBlank @Email String email,
        String contrasena) {
}
