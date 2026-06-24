package com.novatech.store.dto;

/**
 * DTO `IntegracionCanalResponse`: objeto de transferencia (entrada/salida) sin mapeo directo 1:1 a tabla.
 */
public record IntegracionCanalResponse(
        Integer idIntegracion,
        String tipo,
        String nombre,
        Boolean activo,
        String estadoConexion,
        String configJson,
        boolean tieneConfiguracion) {

    public static IntegracionCanalResponse enmascarada(
            Integer id, String tipo, String nombre, Boolean activo,
            String estado, String configJsonRaw) {
        return new IntegracionCanalResponse(
                id, tipo, nombre, activo, estado,
                SensitiveDataMasker.enmascararJson(configJsonRaw),
                configJsonRaw != null && !configJsonRaw.isBlank());
    }
}
