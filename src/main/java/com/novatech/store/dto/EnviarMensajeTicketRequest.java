package com.novatech.store.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EnviarMensajeTicketRequest(
        @NotBlank(message = "El mensaje es obligatorio.")
        @Size(min = 1, max = 5000, message = "El mensaje debe tener entre 1 y 5000 caracteres.")
        String cuerpo) {
}
