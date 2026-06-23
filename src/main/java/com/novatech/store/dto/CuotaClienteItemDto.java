package com.novatech.store.dto;

import com.novatech.store.entity.Cuota;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record CuotaClienteItemDto(
        Integer idCuota,
        Integer numeroCuota,
        BigDecimal monto,
        LocalDate fechaVencimiento,
        LocalDateTime fechaPago,
        String estado) {

    public static CuotaClienteItemDto desde(Cuota c) {
        return new CuotaClienteItemDto(
                c.getIdCuota(),
                c.getNumeroCuota(),
                c.getMonto(),
                c.getFechaVencimiento(),
                c.getFechaPago(),
                c.getEstado());
    }
}
