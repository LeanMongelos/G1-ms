package com.novatech.store.dto;

import com.novatech.store.validation.ValidationPatterns;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ActualizarPerfilClienteRequest(
        @Size(max = 200, message = "La direccion no puede superar 200 caracteres.")
        String direccion,
        @Size(max = 100, message = "La ciudad no puede superar 100 caracteres.")
        String ciudad,
        @Pattern(regexp = ValidationPatterns.TELEFONO_OPCIONAL, message = "El telefono no tiene un formato valido.")
        String telefono,
        @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres.")
        @Pattern(regexp = ValidationPatterns.NOMBRE_OPCIONAL, message = "El nombre contiene caracteres no permitidos.")
        String nombre,
        @Email(message = "El email no tiene un formato valido.")
        @Pattern(regexp = ValidationPatterns.EMAIL_OPCIONAL, message = "El email no tiene un formato valido.")
        String email) {
}
