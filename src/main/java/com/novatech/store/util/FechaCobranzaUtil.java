package com.novatech.store.util;

import java.time.LocalDate;

/** Ventana de cobro: del 1 al 10 de cada mes. Vencimiento = día 10. */
public final class FechaCobranzaUtil {

    public static final int DIA_VENCIMIENTO = 10;

    private FechaCobranzaUtil() {
    }

    /** Primer vencimiento (día 10) desde la fecha de referencia. */
    public static LocalDate primerVencimiento(LocalDate referencia) {
        LocalDate mesActual = referencia.withDayOfMonth(DIA_VENCIMIENTO);
        if (!referencia.isAfter(mesActual)) {
            return mesActual;
        }
        return referencia.plusMonths(1).withDayOfMonth(DIA_VENCIMIENTO);
    }

    public static LocalDate vencimientoCuota(LocalDate referenciaEmision, int numeroCuota) {
        return primerVencimiento(referenciaEmision).plusMonths(numeroCuota - 1L);
    }

    public static boolean enVentanaCobro(LocalDate hoy) {
        return hoy.getDayOfMonth() >= 1 && hoy.getDayOfMonth() <= DIA_VENCIMIENTO;
    }
}
