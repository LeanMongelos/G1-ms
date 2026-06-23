package com.novatech.store.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CrearDevolucionRequest(
        @NotNull(message = "El id del pedido es obligatorio.")
        @Positive(message = "El id del pedido debe ser positivo.")
        Integer idPedido,
        @NotBlank(message = "El motivo es obligatorio.")
        @Size(max = 100, message = "El motivo no puede superar 100 caracteres.")
        String motivo,
        @Size(max = 2000, message = "La descripcion no puede superar 2000 caracteres.")
        String descripcion,
        @Size(max = 5000, message = "Las lineas no pueden superar 5000 caracteres.")
        String lineasJson) {
}
