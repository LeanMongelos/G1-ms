package com.novatech.store.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record PrestamoClienteDto(
        Integer idPlan,
        Integer idPedido,
        Integer cantidadCuotas,
        BigDecimal interes,
        String estadoPlan,
        BigDecimal totalFinanciado,
        Integer cuotasPagadas,
        /** Número de la cuota que el cliente debe abonar ahora (null si finalizó). */
        Integer cuotaActual,
        Integer idCuotaActual,
        BigDecimal saldoPendiente,
        BigDecimal montoProximaCuota,
        LocalDate vencimientoProximaCuota,
        List<CuotaClienteItemDto> cuotas) {
}
