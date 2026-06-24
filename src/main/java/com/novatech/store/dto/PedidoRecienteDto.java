package com.novatech.store.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO `PedidoRecienteDto`: objeto de transferencia (entrada/salida) sin mapeo directo 1:1 a tabla.
 */
public class PedidoRecienteDto {

    private Integer idPedido;
    private String clienteNombre;
    private BigDecimal total;
    private String estado;
    private LocalDateTime fecha;

    public Integer getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(Integer idPedido) {
        this.idPedido = idPedido;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
}
