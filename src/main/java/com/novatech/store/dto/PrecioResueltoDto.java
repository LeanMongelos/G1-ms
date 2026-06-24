package com.novatech.store.dto;

import java.math.BigDecimal;

/**
 * DTO `PrecioResueltoDto`: objeto de transferencia (entrada/salida) sin mapeo directo 1:1 a tabla.
 */
public class PrecioResueltoDto {

    private Integer idProducto;
    private String codigoLista;
    private String nombreLista;
    private BigDecimal precioBase;
    private BigDecimal descuentoGlobal;
    private BigDecimal descuentoUnitario;
    private BigDecimal precioFijo;
    private BigDecimal descuentoEfectivoPorcentaje;
    private BigDecimal precioEfectivo;
    private boolean usaPrecioFijo;
    private boolean usaDescuentoUnitario;

    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public String getCodigoLista() {
        return codigoLista;
    }

    public void setCodigoLista(String codigoLista) {
        this.codigoLista = codigoLista;
    }

    public String getNombreLista() {
        return nombreLista;
    }

    public void setNombreLista(String nombreLista) {
        this.nombreLista = nombreLista;
    }

    public BigDecimal getPrecioBase() {
        return precioBase;
    }

    public void setPrecioBase(BigDecimal precioBase) {
        this.precioBase = precioBase;
    }

    public BigDecimal getDescuentoGlobal() {
        return descuentoGlobal;
    }

    public void setDescuentoGlobal(BigDecimal descuentoGlobal) {
        this.descuentoGlobal = descuentoGlobal;
    }

    public BigDecimal getDescuentoUnitario() {
        return descuentoUnitario;
    }

    public void setDescuentoUnitario(BigDecimal descuentoUnitario) {
        this.descuentoUnitario = descuentoUnitario;
    }

    public BigDecimal getPrecioFijo() {
        return precioFijo;
    }

    public void setPrecioFijo(BigDecimal precioFijo) {
        this.precioFijo = precioFijo;
    }

    public BigDecimal getDescuentoEfectivoPorcentaje() {
        return descuentoEfectivoPorcentaje;
    }

    public void setDescuentoEfectivoPorcentaje(BigDecimal descuentoEfectivoPorcentaje) {
        this.descuentoEfectivoPorcentaje = descuentoEfectivoPorcentaje;
    }

    public BigDecimal getPrecioEfectivo() {
        return precioEfectivo;
    }

    public void setPrecioEfectivo(BigDecimal precioEfectivo) {
        this.precioEfectivo = precioEfectivo;
    }

    public boolean isUsaPrecioFijo() {
        return usaPrecioFijo;
    }

    public void setUsaPrecioFijo(boolean usaPrecioFijo) {
        this.usaPrecioFijo = usaPrecioFijo;
    }

    public boolean isUsaDescuentoUnitario() {
        return usaDescuentoUnitario;
    }

    public void setUsaDescuentoUnitario(boolean usaDescuentoUnitario) {
        this.usaDescuentoUnitario = usaDescuentoUnitario;
    }
}
