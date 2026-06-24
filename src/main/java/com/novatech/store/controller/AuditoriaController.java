package com.novatech.store.controller;

import com.novatech.store.entity.LogSistema;
import com.novatech.store.entity.RegistroAuditoria;
import com.novatech.store.repository.LogSistemaRepository;
import com.novatech.store.repository.RegistroAuditoriaRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/configuracion")
/**
 * Controlador REST `AuditoriaController`: expone endpoints HTTP JSON para Auditoria. Ruta base en `@RequestMapping` de la clase.
 */
public class AuditoriaController {

    private final RegistroAuditoriaRepository auditoriaRepository;
    private final LogSistemaRepository logRepository;

    public AuditoriaController(RegistroAuditoriaRepository auditoriaRepository,
                               LogSistemaRepository logRepository) {
        this.auditoriaRepository = auditoriaRepository;
        this.logRepository = logRepository;
    }

    @GetMapping("/auditoria/registros")
    public List<RegistroAuditoria> registrosAuditoria(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String entidad,
            @RequestParam(required = false) String usuario) {
        String busqueda = (q == null || q.isBlank()) ? null : q;
        String ent = (entidad == null || entidad.isBlank()) ? null : entidad;
        String usr = (usuario == null || usuario.isBlank()) ? null : usuario;
        return auditoriaRepository.buscar(busqueda, ent, usr);
    }

    @GetMapping("/logs/registros")
    public List<LogSistema> registrosLogs(
            @RequestParam(required = false) String nivel,
            @RequestParam(required = false) String origen,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Integer dias) {
        int retencion = dias != null ? dias : 15;
        LocalDateTime desde = LocalDate.now().minusDays(retencion).atStartOfDay();
        List<LogSistema> lista = logRepository.findDesde(desde);
        return lista.stream()
                .filter(l -> nivel == null || nivel.isBlank() || nivel.equalsIgnoreCase(l.getNivel()))
                .filter(l -> origen == null || origen.isBlank() || origen.equalsIgnoreCase(l.getOrigen()))
                .filter(l -> q == null || q.isBlank()
                        || (l.getMensaje() != null && l.getMensaje().toLowerCase().contains(q.toLowerCase())))
                .limit(100)
                .toList();
    }
}
