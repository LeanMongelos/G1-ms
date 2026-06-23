package com.novatech.store.dto;

import jakarta.validation.constraints.NotBlank;

public record EnviarMensajeTicketRequest(@NotBlank String cuerpo) {
}
