package com.novatech.store.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CrearDevolucionRequest(
        @NotNull Integer idPedido,
        @NotBlank String motivo,
        String descripcion,
        String lineasJson) {
}
