package com.novatech.store.dto;

import com.novatech.store.validation.ValidationPatterns;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO `ActualizarPerfilRequest`: objeto de transferencia (entrada/salida) sin mapeo directo 1:1 a tabla.
 */
public record ActualizarPerfilRequest(
        @NotBlank(message = "El nombre es obligatorio.")
        @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres.")
        @Pattern(regexp = ValidationPatterns.NOMBRE, message = "El nombre contiene caracteres no permitidos.")
        String nombre,
        @NotBlank(message = "El email es obligatorio.")
        @Email(message = "El email no tiene un formato valido.")
        @Pattern(regexp = ValidationPatterns.EMAIL, message = "El email no tiene un formato valido.")
        String email,
        @Size(min = 6, message = "La contrasena debe tener al menos 6 caracteres.")
        String contrasena) {
}
