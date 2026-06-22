package com.novatech.store.service;

import com.novatech.store.dto.CrmResumenResponse;
import com.novatech.store.entity.Campana;
import com.novatech.store.entity.MensajeCliente;
import com.novatech.store.entity.Promocion;
import com.novatech.store.repository.CampanaRepository;
import com.novatech.store.repository.CuotaRepository;
import com.novatech.store.repository.FacturaRepository;
import com.novatech.store.repository.InteraccionCrmRepository;
import com.novatech.store.repository.MensajeClienteRepository;
import com.novatech.store.repository.PedidoRepository;
import com.novatech.store.repository.PromocionRepository;
import com.novatech.store.repository.UsuarioRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class CrmService {

    private final UsuarioRepository usuarioRepository;
    private final PedidoRepository pedidoRepository;
    private final PromocionRepository promocionRepository;
    private final CampanaRepository campanaRepository;
    private final CuotaRepository cuotaRepository;
    private final FacturaRepository facturaRepository;
    private final InteraccionCrmRepository interaccionRepository;
    private final MensajeClienteRepository mensajeRepository;
    private final CuotaService cuotaService;

    public CrmService(UsuarioRepository usuarioRepository,
                      PedidoRepository pedidoRepository,
                      PromocionRepository promocionRepository,
                      CampanaRepository campanaRepository,
                      CuotaRepository cuotaRepository,
                      FacturaRepository facturaRepository,
                      InteraccionCrmRepository interaccionRepository,
                      MensajeClienteRepository mensajeRepository,
                      CuotaService cuotaService) {
        this.usuarioRepository = usuarioRepository;
        this.pedidoRepository = pedidoRepository;
        this.promocionRepository = promocionRepository;
        this.campanaRepository = campanaRepository;
        this.cuotaRepository = cuotaRepository;
        this.facturaRepository = facturaRepository;
        this.interaccionRepository = interaccionRepository;
        this.mensajeRepository = mensajeRepository;
        this.cuotaService = cuotaService;
    }

    public CrmResumenResponse resumen() {
        cuotaService.actualizarVencidas();

        CrmResumenResponse res = new CrmResumenResponse();

        Set<Integer> activos = new HashSet<>();
        pedidoRepository.findAll().forEach(p -> {
            if (p.getUsuario() != null && p.getUsuario().getIdUsuario() != null) {
                activos.add(p.getUsuario().getIdUsuario());
            }
        });
        res.setClientesActivos(activos.size());

        res.setPromocionesActivas(promocionRepository.findAll().stream()
                .filter(p -> "ACTIVA".equals(p.getEstado()))
                .count());

        res.setCampanasPendientes(campanaRepository.findAll().stream()
                .filter(c -> "BORRADOR".equals(c.getEstado()) || "PROGRAMADA".equals(c.getEstado()))
                .count());

        res.setCuotasVencidas(cuotaRepository.findByEstado("VENCIDA").size());

        LocalDate limite = LocalDate.now().plusDays(7);
        res.setCuotasPorVencer(cuotaRepository.findAll().stream()
                .filter(c -> "PENDIENTE".equals(c.getEstado()))
                .filter(c -> !c.getFechaVencimiento().isBefore(LocalDate.now()))
                .filter(c -> !c.getFechaVencimiento().isAfter(limite))
                .count());

        res.setFacturasEmitidas(facturaRepository.findAll().stream()
                .filter(f -> "EMITIDA".equals(f.getEstado()))
                .count());

        res.setInteraccionesAbiertas(interaccionRepository.findByEstado("ABIERTA").size());

        LocalDateTime inicioMes = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        res.setMensajesEnviadosMes(mensajeRepository.findAll().stream()
                .filter(m -> "ENVIADO".equals(m.getEstado()))
                .filter(m -> m.getFechaEnvio() != null && !m.getFechaEnvio().isBefore(inicioMes))
                .count());

        return res;
    }
}
