package com.novatech.store.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * DTO `ConfirmarOrdenLineaRequest`: objeto de transferencia (entrada/salida) sin mapeo directo 1:1 a tabla.
 */
public class ConfirmarOrdenLineaRequest {

    @NotNull(message = "El id del producto es obligatorio.")
    private Integer idProducto;

    @NotNull(message = "La cantidad es obligatoria.")
    @Min(value = 1, message = "La cantidad debe ser al menos 1.")
    private Integer cantidad;

    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
}
