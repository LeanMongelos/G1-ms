package com.novatech.store.dto;

import com.novatech.store.entity.DetallePedido;
import com.novatech.store.entity.Envio;
import com.novatech.store.entity.Pago;
import com.novatech.store.entity.Pedido;
import com.novatech.store.entity.PlanCuotas;
import java.math.BigDecimal;
import java.util.List;

public class PedidoDetalleResponse {

    private Pedido pedido;
    private List<DetallePedido> detalles;
    private List<Pago> pagos;
    private Envio envio;
    private PlanCuotas planCuotas;
    private BigDecimal saldoPendiente;
    private Integer idFactura;

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public List<DetallePedido> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetallePedido> detalles) {
        this.detalles = detalles;
    }

    public List<Pago> getPagos() {
        return pagos;
    }

    public void setPagos(List<Pago> pagos) {
        this.pagos = pagos;
    }

    public Envio getEnvio() {
        return envio;
    }

    public void setEnvio(Envio envio) {
        this.envio = envio;
    }

    public PlanCuotas getPlanCuotas() {
        return planCuotas;
    }

    public void setPlanCuotas(PlanCuotas planCuotas) {
        this.planCuotas = planCuotas;
    }

    public BigDecimal getSaldoPendiente() {
        return saldoPendiente;
    }

    public void setSaldoPendiente(BigDecimal saldoPendiente) {
        this.saldoPendiente = saldoPendiente;
    }

    public Integer getIdFactura() {
        return idFactura;
    }

    public void setIdFactura(Integer idFactura) {
        this.idFactura = idFactura;
    }
}
