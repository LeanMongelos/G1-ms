package com.novatech.store.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CrearTicketClienteRequest(
        @NotBlank(message = "El asunto es obligatorio.")
        @Size(max = 200, message = "El asunto no puede superar 200 caracteres.")
        String asunto,
        @NotBlank(message = "El mensaje es obligatorio.")
        @Size(min = 5, max = 5000, message = "El mensaje debe tener entre 5 y 5000 caracteres.")
        String cuerpo,
        @Size(max = 200, message = "Las etiquetas no pueden superar 200 caracteres.")
        String etiquetas,
        @Positive(message = "El id del pedido debe ser positivo.")
        Integer idPedido) {
}
