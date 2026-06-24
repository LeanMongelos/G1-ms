package com.novatech.store.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * DTO `ListaPrecioUpdateRequest`: objeto de transferencia (entrada/salida) sin mapeo directo 1:1 a tabla.
 */
public class ListaPrecioUpdateRequest {

    @Size(max = 100, message = "El nombre no puede superar 100 caracteres.")
    private String nombre;

    @Size(max = 500, message = "La descripcion no puede superar 500 caracteres.")
    private String descripcion;

    @DecimalMin(value = "0", message = "El descuento no puede ser negativo.")
    @DecimalMax(value = "100", message = "El descuento no puede superar 100%.")
    private BigDecimal descuentoGlobal;

    private Boolean activo;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getDescuentoGlobal() {
        return descuentoGlobal;
    }

    public void setDescuentoGlobal(BigDecimal descuentoGlobal) {
        this.descuentoGlobal = descuentoGlobal;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}
