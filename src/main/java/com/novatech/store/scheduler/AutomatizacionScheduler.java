package com.novatech.store.scheduler;

import com.novatech.store.entity.Campana;
import com.novatech.store.repository.CampanaRepository;
import com.novatech.store.service.CampanaService;
import com.novatech.store.service.CobranzaCrmService;
import com.novatech.store.service.CuotaService;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Componente backend `AutomatizacionScheduler` del paquete `scheduler`.
 */
@Component
public class AutomatizacionScheduler {

    private static final Logger log = LoggerFactory.getLogger(AutomatizacionScheduler.class);

    private final CuotaService cuotaService;
    private final CampanaRepository campanaRepository;
    private final CampanaService campanaService;
    private final CobranzaCrmService cobranzaCrmService;

    public AutomatizacionScheduler(CuotaService cuotaService,
                                   CampanaRepository campanaRepository,
                                   CampanaService campanaService,
                                   CobranzaCrmService cobranzaCrmService) {
        this.cuotaService = cuotaService;
        this.campanaRepository = campanaRepository;
        this.campanaService = campanaService;
        this.cobranzaCrmService = cobranzaCrmService;
    }

    /** Marca cuotas vencidas cada hora. */
    @Scheduled(cron = "0 0 * * * *")
    public void actualizarCuotasVencidas() {
        cuotaService.actualizarVencidas();
    }

    /** Envía campañas programadas cuya fecha ya pasó. */
    @Scheduled(cron = "0 */5 * * * *")
    public void enviarCampanasProgramadas() {
        LocalDateTime ahora = LocalDateTime.now();
        List<Campana> pendientes = campanaRepository.findAll().stream()
                .filter(c -> "PROGRAMADA".equals(c.getEstado()))
                .filter(c -> c.getFechaProgramada() != null && !c.getFechaProgramada().isAfter(ahora))
                .toList();

        for (Campana campana : pendientes) {
            try {
                campanaService.enviar(campana.getIdCampana());
                log.info("Campaña {} enviada automáticamente", campana.getIdCampana());
            } catch (Exception e) {
                log.warn("No se pudo enviar campaña {}: {}", campana.getIdCampana(), e.getMessage());
            }
        }
    }

    /** Aviso automático CRM por cuotas vencidas (post ventana 1-10). */
    @Scheduled(cron = "0 0 9 * * *")
    public void recordatorioCuotasVencidas() {
        int enviados = cobranzaCrmService.notificarCuotasVencidasSinAviso();
        if (enviados > 0) {
            log.info("Avisos CRM de deuda vencida enviados: {}", enviados);
        }
    }

    /** Reintento al mediodía por si hubo cuotas marcadas vencidas en la mañana. */
    @Scheduled(cron = "0 0 12 * * *")
    public void recordatorioCuotasVencidasMediodia() {
        cobranzaCrmService.notificarCuotasVencidasSinAviso();
    }
}
