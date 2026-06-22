package com.novatech.store.dto;

import java.util.List;

public class RemitoRequest {

    private Integer idCliente;
    private Integer pedidoId;
    private Integer presupuestoId;
    private String direccionEntrega;
    private String notas;
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
