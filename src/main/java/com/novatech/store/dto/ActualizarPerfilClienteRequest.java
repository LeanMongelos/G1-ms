package com.novatech.store.dto;

import jakarta.validation.constraints.Email;

public record ActualizarPerfilClienteRequest(
        String direccion,
        String ciudad,
        String telefono,
        String nombre,
        @Email String email) {
}
