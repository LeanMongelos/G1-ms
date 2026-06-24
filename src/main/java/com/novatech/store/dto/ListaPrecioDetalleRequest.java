package com.novatech.store.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * DTO `ListaPrecioDetalleRequest`: objeto de transferencia (entrada/salida) sin mapeo directo 1:1 a tabla.
 */
public class ListaPrecioDetalleRequest {

    @NotNull(message = "El id del producto es obligatorio.")
    @Positive(message = "El id del producto debe ser positivo.")
    private Integer idProducto;

    @DecimalMin(value = "0", message = "El descuento no puede ser negativo.")
    @DecimalMax(value = "100", message = "El descuento no puede superar 100%.")
    private BigDecimal descuentoPorcentaje;

    @DecimalMin(value = "0.01", message = "El precio fijo debe ser mayor a cero.")
    private BigDecimal precioFijo;

    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public BigDecimal getDescuentoPorcentaje() {
        return descuentoPorcentaje;
    }

    public void setDescuentoPorcentaje(BigDecimal descuentoPorcentaje) {
        this.descuentoPorcentaje = descuentoPorcentaje;
    }

    public BigDecimal getPrecioFijo() {
        return precioFijo;
    }

    public void setPrecioFijo(BigDecimal precioFijo) {
        this.precioFijo = precioFijo;
    }
}
