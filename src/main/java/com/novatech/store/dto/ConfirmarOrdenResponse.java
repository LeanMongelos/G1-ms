package com.novatech.store.dto;

import com.novatech.store.entity.Envio;
import com.novatech.store.entity.Pago;
import com.novatech.store.entity.Pedido;
import com.novatech.store.entity.PlanCuotas;

/**
 * DTO `ConfirmarOrdenResponse`: objeto de transferencia (entrada/salida) sin mapeo directo 1:1 a tabla.
 */
public class ConfirmarOrdenResponse {

    private Pedido pedido;
    private Pago pago;
    private Envio envio;
    private PlanCuotas planCuotas;

    public ConfirmarOrdenResponse() {
    }

    public ConfirmarOrdenResponse(Pedido pedido, Pago pago, Envio envio, PlanCuotas planCuotas) {
        this.pedido = pedido;
        this.pago = pago;
        this.envio = envio;
        this.planCuotas = planCuotas;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public Pago getPago() {
        return pago;
    }

    public void setPago(Pago pago) {
        this.pago = pago;
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
}
