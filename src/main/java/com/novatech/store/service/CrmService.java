package com.novatech.store.service;

import com.novatech.store.dto.CrmResumenResponse;
import org.springframework.stereotype.Service;

/**
 * Servicio `CrmService`: reglas de negocio, transacciones y orquestación de Crm. Los controllers delegan aquí; no accede HTTP directamente.
 */
@Service
public class CrmService {

    private final CrmMetricsService crmMetricsService;
    private final CuotaService cuotaService;

    public CrmService(CrmMetricsService crmMetricsService, CuotaService cuotaService) {
        this.crmMetricsService = crmMetricsService;
        this.cuotaService = cuotaService;
    }

    public CrmResumenResponse resumen() {
        cuotaService.actualizarVencidas();

        CrmResumenResponse res = new CrmResumenResponse();
        res.setClientesActivos(crmMetricsService.contarClientesActivos());
        res.setPromocionesActivas(crmMetricsService.contarPromocionesActivas());
        res.setCampanasPendientes(crmMetricsService.contarCampanasPendientes());
        res.setCuotasVencidas(crmMetricsService.contarCuotasVencidas());
        res.setCuotasPorVencer(crmMetricsService.contarCuotasPorVencer());
        res.setFacturasEmitidas(crmMetricsService.contarFacturasEmitidas());
        res.setInteraccionesAbiertas(crmMetricsService.contarInteraccionesAbiertas());
        res.setMensajesEnviadosMes(crmMetricsService.contarMensajesEnviadosMes());
        return res;
    }
}
