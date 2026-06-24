package com.novatech.store.service;

import com.novatech.store.entity.Conversacion;
import com.novatech.store.repository.CampanaRepository;
import com.novatech.store.repository.ConversacionRepository;
import com.novatech.store.repository.CuotaRepository;
import com.novatech.store.repository.FacturaRepository;
import com.novatech.store.repository.InteraccionCrmRepository;
import com.novatech.store.repository.MensajeClienteRepository;
import com.novatech.store.repository.PedidoRepository;
import com.novatech.store.repository.PromocionRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import org.springframework.stereotype.Service;

/**
 * Servicio `CrmMetricsService`: reglas de negocio, transacciones y orquestación de CrmMetrics. Los controllers delegan aquí; no accede HTTP directamente.
 */
@Service
public class CrmMetricsService {

    private static final int DIAS_CUOTAS_POR_VENCER = 7;

    private final PedidoRepository pedidoRepository;
    private final PromocionRepository promocionRepository;
    private final CampanaRepository campanaRepository;
    private final CuotaRepository cuotaRepository;
    private final ConversacionRepository conversacionRepository;
    private final FacturaRepository facturaRepository;
    private final InteraccionCrmRepository interaccionRepository;
    private final MensajeClienteRepository mensajeRepository;

    public CrmMetricsService(PedidoRepository pedidoRepository,
                             PromocionRepository promocionRepository,
                             CampanaRepository campanaRepository,
                             CuotaRepository cuotaRepository,
                             ConversacionRepository conversacionRepository,
                             FacturaRepository facturaRepository,
                             InteraccionCrmRepository interaccionRepository,
                             MensajeClienteRepository mensajeRepository) {
        this.pedidoRepository = pedidoRepository;
        this.promocionRepository = promocionRepository;
        this.campanaRepository = campanaRepository;
        this.cuotaRepository = cuotaRepository;
        this.conversacionRepository = conversacionRepository;
        this.facturaRepository = facturaRepository;
        this.interaccionRepository = interaccionRepository;
        this.mensajeRepository = mensajeRepository;
    }

    public long contarClientesActivos() {
        Set<Integer> activos = new HashSet<>();
        pedidoRepository.findAll().forEach(p -> {
            if (p.getUsuario() != null && p.getUsuario().getIdUsuario() != null) {
                activos.add(p.getUsuario().getIdUsuario());
            }
        });
        return activos.size();
    }

    public long contarPromocionesActivas() {
        return promocionRepository.findAll().stream()
                .filter(p -> "ACTIVA".equals(p.getEstado()))
                .count();
    }

    public long contarCampanasPendientes() {
        return campanaRepository.findAll().stream()
                .filter(c -> "BORRADOR".equals(c.getEstado()) || "PROGRAMADA".equals(c.getEstado()))
                .count();
    }

    public long contarCuotasVencidas() {
        return cuotaRepository.findByEstado("VENCIDA").size();
    }

    public long contarCuotasPorVencer() {
        LocalDate hoy = LocalDate.now();
        LocalDate limite = hoy.plusDays(DIAS_CUOTAS_POR_VENCER);
        return cuotaRepository.findAll().stream()
                .filter(c -> "PENDIENTE".equals(c.getEstado()))
                .filter(c -> !c.getFechaVencimiento().isBefore(hoy))
                .filter(c -> !c.getFechaVencimiento().isAfter(limite))
                .count();
    }

    public long contarConversacionesPendientes() {
        return conversacionRepository.findAll().stream()
                .filter(CrmMetricsService::conversacionPendiente)
                .count();
    }

    public long contarFacturasEmitidas() {
        return facturaRepository.findAll().stream()
                .filter(f -> "EMITIDA".equals(f.getEstado()))
                .count();
    }

    public long contarInteraccionesAbiertas() {
        return interaccionRepository.findByEstado("ABIERTA").size();
    }

    public long contarMensajesEnviadosMes() {
        LocalDateTime inicioMes = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        return mensajeRepository.findAll().stream()
                .filter(m -> "ENVIADO".equals(m.getEstado()))
                .filter(m -> m.getFechaEnvio() != null && !m.getFechaEnvio().isBefore(inicioMes))
                .count();
    }

    public static boolean conversacionPendiente(Conversacion c) {
        if (c == null) {
            return false;
        }
        String est = c.getEstado() != null ? c.getEstado().toUpperCase() : "";
        return "ABIERTA".equals(est) || "PENDIENTE".equals(est) || "NUEVA".equals(est)
                || "EN_PROCESO".equals(est);
    }
}
