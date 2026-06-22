package com.novatech.store.service;

import com.novatech.store.dto.ConversacionResumenResponse;
import com.novatech.store.entity.Conversacion;
import com.novatech.store.entity.MensajeConversacion;
import com.novatech.store.entity.PerfilCliente;
import com.novatech.store.entity.Usuario;
import com.novatech.store.exception.ReglaNegocioException;
import com.novatech.store.exception.ResourceNotFoundException;
import com.novatech.store.util.UsuarioRolUtil;
import com.novatech.store.repository.ConversacionRepository;
import com.novatech.store.repository.MensajeConversacionRepository;
import com.novatech.store.repository.PerfilClienteRepository;
import com.novatech.store.repository.UsuarioRepository;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConversacionService {

    private final ConversacionRepository conversacionRepository;
    private final MensajeConversacionRepository mensajeRepository;
    private final PerfilClienteRepository perfilRepository;
    private final UsuarioRepository usuarioRepository;

    public ConversacionService(ConversacionRepository conversacionRepository,
                               MensajeConversacionRepository mensajeRepository,
                               PerfilClienteRepository perfilRepository,
                               UsuarioRepository usuarioRepository) {
        this.conversacionRepository = conversacionRepository;
        this.mensajeRepository = mensajeRepository;
        this.perfilRepository = perfilRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public ConversacionResumenResponse resumen() {
        ConversacionResumenResponse res = new ConversacionResumenResponse();
        long pendientes = 0;
        long sinResolver = 0;
        for (Conversacion c : conversacionRepository.findAll()) {
            String estado = c.getEstado() != null ? c.getEstado().toUpperCase() : "";
            if ("PENDIENTE".equals(estado)) {
                pendientes++;
                sinResolver++;
            } else if ("EN_PROCESO".equals(estado)) {
                sinResolver++;
            }
        }
        res.setPendientes(pendientes);
        res.setSinResolver(sinResolver);
        return res;
    }

    public List<Conversacion> listar(String canal, String estado, String busqueda) {
        List<Conversacion> lista;
        if (canal != null && !canal.isBlank() && estado != null && !estado.isBlank()) {
            lista = conversacionRepository.findByCanalIgnoreCaseAndEstadoIgnoreCase(canal, estado);
        } else if (canal != null && !canal.isBlank()) {
            lista = conversacionRepository.findByCanalIgnoreCase(canal);
        } else if (estado != null && !estado.isBlank()) {
            lista = conversacionRepository.findByEstadoIgnoreCase(estado);
        } else {
            lista = conversacionRepository.findAll();
        }
        if (busqueda != null && !busqueda.isBlank()) {
            String q = busqueda.toLowerCase();
            lista = lista.stream()
                    .filter(c -> contiene(c.getContactoNombre(), q)
                            || contiene(c.getContactoEmail(), q)
                            || contiene(c.getAsunto(), q)
                            || contiene(c.getVistaPrevia(), q))
                    .toList();
        }
        return lista.stream()
                .sorted(Comparator.comparing(Conversacion::getUltimaActividad,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
    }

    public Conversacion obtener(Integer id) {
        return conversacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conversación no encontrada: " + id));
    }

    public List<MensajeConversacion> mensajes(Integer idConversacion) {
        obtener(idConversacion);
        return mensajeRepository.findByConversacionIdConversacionOrderByFechaAsc(idConversacion);
    }

    @Transactional
    public MensajeConversacion enviarMensaje(Integer idConversacion, MensajeConversacion mensaje) {
        Conversacion conv = obtener(idConversacion);
        mensaje.setIdMensaje(null);
        mensaje.setConversacion(conv);
        mensaje.setDireccion("SALIENTE");
        if (mensaje.getFecha() == null) {
            mensaje.setFecha(LocalDateTime.now());
        }
        if (mensaje.getRemitenteNombre() == null || mensaje.getRemitenteNombre().isBlank()) {
            mensaje.setRemitenteNombre(conv.getAsignadoA() != null
                    ? conv.getAsignadoA().getNombre() : "Equipo NovaTech");
        }
        MensajeConversacion guardado = mensajeRepository.save(mensaje);
        conv.setVistaPrevia(truncar(mensaje.getCuerpo(), 120));
        conv.setUltimaActividad(mensaje.getFecha());
        conversacionRepository.save(conv);
        return guardado;
    }

    public Conversacion actualizar(Integer id, Conversacion datos) {
        Conversacion conv = obtener(id);
        if (datos.getEstado() != null) {
            conv.setEstado(datos.getEstado());
        }
        if (datos.getEtiquetas() != null) {
            conv.setEtiquetas(datos.getEtiquetas());
        }
        if (datos.getAsignadoA() != null && datos.getAsignadoA().getIdUsuario() != null) {
            Usuario u = usuarioRepository.findById(datos.getAsignadoA().getIdUsuario())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Usuario no encontrado: " + datos.getAsignadoA().getIdUsuario()));
            if (!UsuarioRolUtil.esEmpleado(u.getRol())) {
                throw new ReglaNegocioException(
                        "Solo se puede asignar la conversación a empleados de la empresa, no a clientes.");
            }
            conv.setAsignadoA(u);
        }
        if (datos.getCliente() != null && datos.getCliente().getIdCliente() != null) {
            PerfilCliente perfil = perfilRepository.findById(datos.getCliente().getIdCliente())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Cliente no encontrado: " + datos.getCliente().getIdCliente()));
            conv.setCliente(perfil);
        }
        return conversacionRepository.save(conv);
    }

    private boolean contiene(String texto, String q) {
        return texto != null && texto.toLowerCase().contains(q);
    }

    private String truncar(String texto, int max) {
        if (texto == null) {
            return "";
        }
        return texto.length() <= max ? texto : texto.substring(0, max) + "…";
    }
}
