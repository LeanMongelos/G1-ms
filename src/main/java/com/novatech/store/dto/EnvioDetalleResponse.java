package com.novatech.store.dto;

import com.novatech.store.entity.DetallePedido;
import com.novatech.store.entity.Envio;
import com.novatech.store.entity.Factura;
import com.novatech.store.entity.Pago;
import com.novatech.store.entity.Pedido;
import java.math.BigDecimal;
import java.util.List;

/**
 * DTO `EnvioDetalleResponse`: objeto de transferencia (entrada/salida) sin mapeo directo 1:1 a tabla.
 */
public class EnvioDetalleResponse {

    private Envio envio;
    private Pedido pedido;
    private String clienteNombre;
    private String clienteEmail;
    private List<DetallePedido> lineas;
    private List<Pago> pagos;
    private Factura factura;
    private BigDecimal saldoPendiente;

    public Envio getEnvio() {
        return envio;
    }

    public void setEnvio(Envio envio) {
        this.envio = envio;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }

    public String getClienteEmail() {
        return clienteEmail;
    }

    public void setClienteEmail(String clienteEmail) {
        this.clienteEmail = clienteEmail;
    }

    public List<DetallePedido> getLineas() {
        return lineas;
    }

    public void setLineas(List<DetallePedido> lineas) {
        this.lineas = lineas;
    }

    public List<Pago> getPagos() {
        return pagos;
    }

    public void setPagos(List<Pago> pagos) {
        this.pagos = pagos;
    }

    public Factura getFactura() {
        return factura;
    }

    public void setFactura(Factura factura) {
        this.factura = factura;
    }

    public BigDecimal getSaldoPendiente() {
        return saldoPendiente;
    }

    public void setSaldoPendiente(BigDecimal saldoPendiente) {
        this.saldoPendiente = saldoPendiente;
    }
}
