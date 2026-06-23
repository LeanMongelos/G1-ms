package com.novatech.store.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

/** DTO unificado para líneas de presupuesto, remito y factura. */
public class LineaComprobanteDto {

    @NotNull(message = "El id del producto es obligatorio.")
    @Positive(message = "El id del producto debe ser positivo.")
    private Integer idProducto;

    @NotNull(message = "La cantidad es obligatoria.")
    @Min(value = 1, message = "La cantidad debe ser al menos 1.")
    private Integer cantidad;

    @NotNull(message = "El precio unitario es obligatorio.")
    @DecimalMin(value = "0.01", message = "El precio unitario debe ser mayor a cero.")
    private BigDecimal precioUnitario;

    @DecimalMin(value = "0", message = "El descuento no puede ser negativo.")
    @DecimalMax(value = "100", message = "El descuento no puede superar 100%.")
    private BigDecimal descuentoPorcentaje;

    @Size(max = 500, message = "La descripcion no puede superar 500 caracteres.")
    private String descripcion;

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

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public BigDecimal getDescuentoPorcentaje() {
        return descuentoPorcentaje;
    }

    public void setDescuentoPorcentaje(BigDecimal descuentoPorcentaje) {
        this.descuentoPorcentaje = descuentoPorcentaje;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
