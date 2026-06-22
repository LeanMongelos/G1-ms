package com.novatech.store.config;

import java.util.Set;

/**
 * Reglas de negocio configurables para facturación desde presupuesto.
 * Para permitir solo APROBADO, reemplazar el Set por {@code Set.of("APROBADO")}.
 */
public final class FacturacionConfig {

    /** Estados de presupuesto desde los cuales se puede generar factura. */
    public static final Set<String> ESTADOS_PRESUPUESTO_FACTURABLES =
            Set.of("APROBADO", "ENVIADO");

    public static final String ESTADO_PRESUPUESTO_FACTURADO = "FACTURADO";

    private FacturacionConfig() {
    }
}
