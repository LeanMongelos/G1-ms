package com.novatech.store.dto;

public class ConversacionResumenResponse {

    /** Conversaciones PENDIENTE (sin atender). */
    private long pendientes;

    /** PENDIENTE + EN_PROCESO (bandeja activa). */
    private long sinResolver;

    public long getPendientes() {
        return pendientes;
    }

    public void setPendientes(long pendientes) {
        this.pendientes = pendientes;
    }

    public long getSinResolver() {
        return sinResolver;
    }

    public void setSinResolver(long sinResolver) {
        this.sinResolver = sinResolver;
    }
}
