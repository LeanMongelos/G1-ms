package com.novatech.store.service;

import com.novatech.store.entity.Conversacion;
import com.novatech.store.entity.Cuota;
import com.novatech.store.entity.InteraccionCrm;
import com.novatech.store.entity.MensajeConversacion;
import com.novatech.store.entity.PerfilCliente;
import com.novatech.store.entity.PlanCuotas;
import com.novatech.store.entity.Usuario;
import com.novatech.store.repository.ConversacionRepository;
import com.novatech.store.repository.CuotaRepository;
import com.novatech.store.repository.InteraccionCrmRepository;
import com.novatech.store.repository.MensajeConversacionRepository;
import com.novatech.store.util.FechaCobranzaUtil;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CobranzaCrmService {

    private static final Logger log = LoggerFactory.getLogger(CobranzaCrmService.class);
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final CuotaRepository cuotaRepository;
    private final CuotaService cuotaService;
    private final ConversacionRepository conversacionRepository;
    private final MensajeConversacionRepository mensajeRepository;
    private final InteraccionCrmRepository interaccionRepository;

    public CobranzaCrmService(CuotaRepository cuotaRepository,
                              CuotaService cuotaService,
                              ConversacionRepository conversacionRepository,
                              MensajeConversacionRepository mensajeRepository,
                              InteraccionCrmRepository interaccionRepository) {
        this.cuotaRepository = cuotaRepository;
        this.cuotaService = cuotaService;
        this.conversacionRepository = conversacionRepository;
        this.mensajeRepository = mensajeRepository;
        this.interaccionRepository = interaccionRepository;
    }

    @Transactional
    public int notificarCuotasVencidasSinAviso() {
        cuotaService.actualizarVencidas();
        List<Cuota> pendientes = cuotaRepository.findByEstado("VENCIDA").stream()
                .filter(c -> !Boolean.TRUE.equals(c.getAvisoVencimientoEnviado()))
                .toList();

        int enviados = 0;
        for (Cuota cuota : pendientes) {
            try {
                notificarCuota(cuota);
                cuota.setAvisoVencimientoEnviado(true);
                cuotaRepository.save(cuota);
                enviados++;
            } catch (Exception e) {
                log.warn("No se pudo notificar cuota {}: {}", cuota.getIdCuota(), e.getMessage());
            }
        }
        return enviados;
    }

    private void notificarCuota(Cuota cuota) {
        PlanCuotas plan = cuota.getPlan();
        if (plan == null || plan.getCliente() == null) {
            return;
        }
        PerfilCliente cliente = plan.getCliente();
        Usuario usuario = cliente.getUsuario();
        String nombre = usuario != null ? usuario.getNombre() : "Cliente";
        String email = usuario != null ? usuario.getEmail() : null;

        String titulo = "Cuota " + cuota.getNumeroCuota() + " vencida";
        String cuerpo = String.format(
                "Estimado/a %s: la cuota %d de su plan venció el %s (ventana de cobro del 1 al %d). "
                        + "Monto pendiente: $%s. Regularice su situación para evitar intereses.",
                nombre,
                cuota.getNumeroCuota(),
                cuota.getFechaVencimiento().format(FMT),
                FechaCobranzaUtil.DIA_VENCIMIENTO,
                cuota.getMonto());

        InteraccionCrm interaccion = new InteraccionCrm();
        interaccion.setCliente(cliente);
        interaccion.setTipo("RECLAMO");
        interaccion.setTitulo(titulo);
        interaccion.setDescripcion(cuerpo);
        interaccion.setPrioridad("ALTA");
        interaccion.setEstado("ABIERTA");
        interaccion.setFecha(LocalDateTime.now());
        interaccionRepository.save(interaccion);

        Conversacion conv = new Conversacion();
        conv.setCanal("EMAIL");
        conv.setContactoNombre(nombre);
        conv.setContactoEmail(email);
        conv.setContactoTelefono(cliente.getTelefono());
        conv.setAsunto(titulo);
        conv.setVistaPrevia(cuerpo.substring(0, Math.min(120, cuerpo.length())));
        conv.setEstado("PENDIENTE");
        conv.setEtiquetas("reclamo,urgente,cobranza");
        conv.setCliente(cliente);
        conv.setFechaCreacion(LocalDateTime.now());
        conv.setUltimaActividad(LocalDateTime.now());
        conv = conversacionRepository.save(conv);

        MensajeConversacion mensaje = new MensajeConversacion();
        mensaje.setConversacion(conv);
        mensaje.setDireccion("SALIENTE");
        mensaje.setRemitenteNombre("Sistema NovaTech — Cobranzas");
        mensaje.setCuerpo(cuerpo);
        mensaje.setFecha(LocalDateTime.now());
        mensajeRepository.save(mensaje);
    }
}
