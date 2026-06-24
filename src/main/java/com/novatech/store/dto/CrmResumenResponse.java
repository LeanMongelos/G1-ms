package com.novatech.store.dto;

/**
 * DTO `CrmResumenResponse`: objeto de transferencia (entrada/salida) sin mapeo directo 1:1 a tabla.
 */
public class CrmResumenResponse {

    private long clientesActivos;
    private long promocionesActivas;
    private long campanasPendientes;
    private long cuotasVencidas;
    private long cuotasPorVencer;
    private long facturasEmitidas;
    private long interaccionesAbiertas;
    private long mensajesEnviadosMes;

    public long getClientesActivos() {
        return clientesActivos;
    }

    public void setClientesActivos(long clientesActivos) {
        this.clientesActivos = clientesActivos;
    }

    public long getPromocionesActivas() {
        return promocionesActivas;
    }

    public void setPromocionesActivas(long promocionesActivas) {
        this.promocionesActivas = promocionesActivas;
    }

    public long getCampanasPendientes() {
        return campanasPendientes;
    }

    public void setCampanasPendientes(long campanasPendientes) {
        this.campanasPendientes = campanasPendientes;
    }

    public long getCuotasVencidas() {
        return cuotasVencidas;
    }

    public void setCuotasVencidas(long cuotasVencidas) {
        this.cuotasVencidas = cuotasVencidas;
    }

    public long getCuotasPorVencer() {
        return cuotasPorVencer;
    }

    public void setCuotasPorVencer(long cuotasPorVencer) {
        this.cuotasPorVencer = cuotasPorVencer;
    }

    public long getFacturasEmitidas() {
        return facturasEmitidas;
    }

    public void setFacturasEmitidas(long facturasEmitidas) {
        this.facturasEmitidas = facturasEmitidas;
    }

    public long getInteraccionesAbiertas() {
        return interaccionesAbiertas;
    }

    public void setInteraccionesAbiertas(long interaccionesAbiertas) {
        this.interaccionesAbiertas = interaccionesAbiertas;
    }

    public long getMensajesEnviadosMes() {
        return mensajesEnviadosMes;
    }

    public void setMensajesEnviadosMes(long mensajesEnviadosMes) {
        this.mensajesEnviadosMes = mensajesEnviadosMes;
    }
}
