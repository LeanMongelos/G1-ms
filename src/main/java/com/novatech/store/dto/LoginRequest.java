package com.novatech.store.dto;

// DTO con los datos que llegan en el cuerpo del pedido POST /auth/login.
// Es lo que manda el frontend cuando alguien intenta iniciar sesion.
public record LoginRequest(
        // Email con el que la persona se registro.
        String email,
        // Contrasena en texto plano que el usuario escribio en el formulario.
        // El backend la compara contra el hash guardado (nunca la guarda asi).
        String contrasena) {
}
