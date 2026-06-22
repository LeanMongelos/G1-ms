package com.novatech.store.dto;

import java.math.BigDecimal;

public class ListaPrecioDetalleRequest {

    private Integer idProducto;
    private BigDecimal descuentoPorcentaje;
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
