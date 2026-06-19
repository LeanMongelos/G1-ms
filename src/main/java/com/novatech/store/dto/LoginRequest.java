package com.novatech.store.dto;

// Anotaciones de Bean Validation (Jakarta) para validar los datos de entrada.
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// DTO con los datos que llegan en el cuerpo del pedido POST /auth/login.
// Es lo que manda el frontend cuando alguien intenta iniciar sesion.
public record LoginRequest(
        // Email con el que la persona se registro. Tiene que venir y con formato valido.
        @NotBlank(message = "El email es obligatorio.")
        @Email(message = "El email no tiene un formato valido.")
        String email,
        // Contrasena en texto plano que el usuario escribio en el formulario.
        // El backend la compara contra el hash guardado (nunca la guarda asi).
        // No puede ir vacia.
        @NotBlank(message = "La contrasena es obligatoria.")
        String contrasena) {
}
