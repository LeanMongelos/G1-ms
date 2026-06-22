package com.novatech.store.dto;

import java.math.BigDecimal;
import java.util.List;

public class GenerarFacturaRequest {

    private Integer pedidoId;
    private Integer presupuestoId;
    private Integer remitoId;
    /** Factura manual: perfil de cliente */
    private Integer clienteId;
    private List<LineaComprobanteDto> lineas;
    private String notas;
    private Integer puntoVenta;
    /** FACTURA_A, FACTURA_B, FACTURA_C */
    private String tipoComprobante;
    /** CONTADO o PRESTAMO_PERSONAL */
    private String formaCobro;
    private Integer cantidadCuotas;
    private BigDecimal interes;

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

    public Integer getRemitoId() {
        return remitoId;
    }

    public void setRemitoId(Integer remitoId) {
        this.remitoId = remitoId;
    }

    public Integer getPuntoVenta() {
        return puntoVenta;
    }

    public void setPuntoVenta(Integer puntoVenta) {
        this.puntoVenta = puntoVenta;
    }

    public String getTipoComprobante() {
        return tipoComprobante;
    }

    public void setTipoComprobante(String tipoComprobante) {
        this.tipoComprobante = tipoComprobante;
    }

    public String getFormaCobro() {
        return formaCobro;
    }

    public void setFormaCobro(String formaCobro) {
        this.formaCobro = formaCobro;
    }

    public Integer getCantidadCuotas() {
        return cantidadCuotas;
    }

    public void setCantidadCuotas(Integer cantidadCuotas) {
        this.cantidadCuotas = cantidadCuotas;
    }

    public BigDecimal getInteres() {
        return interes;
    }

    public void setInteres(BigDecimal interes) {
        this.interes = interes;
    }

    public Integer getClienteId() {
        return clienteId;
    }

    public void setClienteId(Integer clienteId) {
        this.clienteId = clienteId;
    }

    public List<LineaComprobanteDto> getLineas() {
        return lineas;
    }

    public void setLineas(List<LineaComprobanteDto> lineas) {
        this.lineas = lineas;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }
}
