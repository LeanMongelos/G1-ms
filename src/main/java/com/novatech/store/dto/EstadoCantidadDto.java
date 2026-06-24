package com.novatech.store.dto;

/**
 * DTO `EstadoCantidadDto`: objeto de transferencia (entrada/salida) sin mapeo directo 1:1 a tabla.
 */
public class EstadoCantidadDto {

    private String estado;
    private long cantidad;
    private double porcentaje;

    public EstadoCantidadDto() {
    }

    public EstadoCantidadDto(String estado, long cantidad, double porcentaje) {
        this.estado = estado;
        this.cantidad = cantidad;
        this.porcentaje = porcentaje;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public long getCantidad() {
        return cantidad;
    }

    public void setCantidad(long cantidad) {
        this.cantidad = cantidad;
    }

    public double getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(double porcentaje) {
        this.porcentaje = porcentaje;
    }
}
