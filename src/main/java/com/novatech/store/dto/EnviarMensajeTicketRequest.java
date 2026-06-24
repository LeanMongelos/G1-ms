package com.novatech.store.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO `EnviarMensajeTicketRequest`: objeto de transferencia (entrada/salida) sin mapeo directo 1:1 a tabla.
 */
public record EnviarMensajeTicketRequest(
        @NotBlank(message = "El mensaje es obligatorio.")
        @Size(min = 1, max = 5000, message = "El mensaje debe tener entre 1 y 5000 caracteres.")
        String cuerpo) {
}
