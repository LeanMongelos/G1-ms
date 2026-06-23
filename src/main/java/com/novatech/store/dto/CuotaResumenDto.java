package com.novatech.store.dto;

import com.novatech.store.entity.Cuota;
import com.novatech.store.entity.PerfilCliente;
import com.novatech.store.entity.PlanCuotas;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CuotaResumenDto(
        Integer idCuota,
        Integer numeroCuota,
        BigDecimal monto,
        LocalDate fechaVencimiento,
        String estado,
        Integer idPlan,
        Integer idPedido,
        String clienteNombre,
        String clienteEmail) {

    public static CuotaResumenDto desde(Cuota c) {
        PlanCuotas plan = c.getPlan();
        PerfilCliente perfil = plan != null ? plan.getCliente() : null;
        String nombre = null;
        String email = null;
        if (perfil != null && perfil.getUsuario() != null) {
            nombre = perfil.getUsuario().getNombre();
            email = perfil.getUsuario().getEmail();
        }
        return new CuotaResumenDto(
                c.getIdCuota(),
                c.getNumeroCuota(),
                c.getMonto(),
                c.getFechaVencimiento(),
                c.getEstado(),
                plan != null ? plan.getIdPlan() : null,
                plan != null && plan.getPedido() != null ? plan.getPedido().getIdPedido() : null,
                nombre,
                email);
    }
}
