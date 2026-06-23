package com.novatech.store.dto;

import jakarta.validation.constraints.NotBlank;

public record CrearTicketClienteRequest(
        @NotBlank String asunto,
        @NotBlank String cuerpo,
        String etiquetas,
        Integer idPedido) {
}
