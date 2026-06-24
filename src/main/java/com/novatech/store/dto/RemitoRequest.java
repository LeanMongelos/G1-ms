package com.novatech.store.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * DTO `RemitoRequest`: objeto de transferencia (entrada/salida) sin mapeo directo 1:1 a tabla.
 */
public class RemitoRequest {

    @Positive(message = "El id del cliente debe ser positivo.")
    private Integer idCliente;

    @Positive(message = "El id del pedido debe ser positivo.")
    private Integer pedidoId;

    @Positive(message = "El id del presupuesto debe ser positivo.")
    private Integer presupuestoId;

    @Size(max = 300, message = "La direccion de entrega no puede superar 300 caracteres.")
    private String direccionEntrega;

    @Size(max = 500, message = "Las notas no pueden superar 500 caracteres.")
    private String notas;

    @NotEmpty(message = "El remito debe tener al menos una linea.")
    @Valid
    private List<LineaComprobanteDto> lineas;

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public Integer getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(Integer pedidoId) {
        this.pedidoId = pedidoId;
    }

    public Integer getPresupuestoId() {
        return presupuestoId;
    }

    public void setPresupuestoId(Integer presupuestoId) {
        this.presupuestoId = presupuestoId;
    }

    public String getDireccionEntrega() {
        return direccionEntrega;
    }

    public void setDireccionEntrega(String direccionEntrega) {
        this.direccionEntrega = direccionEntrega;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public List<LineaComprobanteDto> getLineas() {
        return lineas;
    }

    public void setLineas(List<LineaComprobanteDto> lineas) {
        this.lineas = lineas;
    }
}
