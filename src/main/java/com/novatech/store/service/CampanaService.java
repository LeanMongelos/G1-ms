package com.novatech.store.service;

import com.novatech.store.entity.Campana;
import com.novatech.store.entity.MensajeCliente;
import com.novatech.store.entity.PerfilCliente;
import com.novatech.store.entity.Promocion;
import com.novatech.store.entity.Usuario;
import com.novatech.store.exception.ReglaNegocioException;
import com.novatech.store.exception.ResourceNotFoundException;
import com.novatech.store.repository.CampanaRepository;
import com.novatech.store.repository.CuotaRepository;
import com.novatech.store.repository.MensajeClienteRepository;
import com.novatech.store.repository.PedidoRepository;
import com.novatech.store.repository.PerfilClienteRepository;
import com.novatech.store.repository.PromocionRepository;
import com.novatech.store.repository.UsuarioRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CampanaService {

    private final CampanaRepository repository;
    private final PromocionRepository promocionRepository;
    private final MensajeClienteRepository mensajeRepository;
    private final UsuarioRepository usuarioRepository;
    private final PerfilClienteRepository perfilRepository;
    private final PedidoRepository pedidoRepository;
    private final CuotaRepository cuotaRepository;

    public CampanaService(CampanaRepository repository,
                          PromocionRepository promocionRepository,
                          MensajeClienteRepository mensajeRepository,
                          UsuarioRepository usuarioRepository,
                          PerfilClienteRepository perfilRepository,
                          PedidoRepository pedidoRepository,
                          CuotaRepository cuotaRepository) {
        this.repository = repository;
        this.promocionRepository = promocionRepository;
        this.mensajeRepository = mensajeRepository;
        this.usuarioRepository = usuarioRepository;
        this.perfilRepository = perfilRepository;
        this.pedidoRepository = pedidoRepository;
        this.cuotaRepository = cuotaRepository;
    }

    public List<Campana> listar() {
        return repository.findAll();
    }

    public Campana obtener(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campaña no encontrada: " + id));
    }

    public Campana crear(Campana campana) {
        campana.setIdCampana(null);
        if (campana.getEstado() == null || campana.getEstado().isBlank()) {
            campana.setEstado("BORRADOR");
        }
        if (campana.getCanal() == null || campana.getCanal().isBlank()) {
            campana.setCanal("EMAIL");
        }
        if (campana.getSegmento() == null || campana.getSegmento().isBlank()) {
            campana.setSegmento("TODOS");
        }
        campana.setCantidadEnviados(0);
        vincularPromocion(campana);
        return repository.save(campana);
    }

    public Campana actualizar(Integer id, Campana datos) {
        Campana campana = obtener(id);
        if ("ENVIADA".equals(campana.getEstado())) {
            throw new ReglaNegocioException("No se puede editar una campaña ya enviada.");
        }
        campana.setNombre(datos.getNombre());
        campana.setTipo(datos.getTipo());
        campana.setPromocion(datos.getPromocion());
        campana.setAsunto(datos.getAsunto());
        campana.setCuerpoMensaje(datos.getCuerpoMensaje());
        campana.setCanal(datos.getCanal());
        campana.setEstado(datos.getEstado());
        campana.setSegmento(datos.getSegmento());
        campana.setFechaProgramada(datos.getFechaProgramada());
        vincularPromocion(campana);
        return repository.save(campana);
    }

    @Transactional
    public Campana enviar(Integer id) {
        Campana campana = obtener(id);
        if ("ENVIADA".equals(campana.getEstado())) {
            throw new ReglaNegocioException("La campaña ya fue enviada.");
        }
        if ("CANCELADA".equals(campana.getEstado())) {
            throw new ReglaNegocioException("La campaña está cancelada.");
        }

        List<Usuario> destinatarios = resolverDestinatarios(campana.getSegmento());
        if (destinatarios.isEmpty()) {
            throw new ReglaNegocioException("No hay destinatarios para el segmento seleccionado.");
        }

        String cuerpo = construirCuerpo(campana);
        LocalDateTime ahora = LocalDateTime.now();
        int enviados = 0;

        for (Usuario usuario : destinatarios) {
            MensajeCliente mensaje = new MensajeCliente();
            mensaje.setCampana(campana);
            mensaje.setUsuario(usuario);
            mensaje.setEmailDestino(usuario.getEmail());
            mensaje.setTelefonoDestino(buscarTelefono(usuario));
            mensaje.setEstado("ENVIADO");
            mensaje.setFechaEnvio(ahora);
            mensaje.setDetalle("[" + campana.getCanal() + "] " + cuerpo);
            mensajeRepository.save(mensaje);
            enviados++;
        }

        campana.setEstado("ENVIADA");
        campana.setFechaEnvio(ahora);
        campana.setCantidadEnviados(enviados);
        return repository.save(campana);
    }

    public List<MensajeCliente> listarMensajes(Integer idCampana) {
        return mensajeRepository.findByCampanaIdCampana(idCampana);
    }

    public void eliminar(Integer id) {
        Campana campana = obtener(id);
        if ("ENVIADA".equals(campana.getEstado())) {
            throw new ReglaNegocioException("No se puede eliminar una campaña enviada.");
        }
        repository.deleteById(id);
    }

    private void vincularPromocion(Campana campana) {
        if (campana.getPromocion() != null && campana.getPromocion().getIdPromocion() != null) {
            Promocion promo = promocionRepository.findById(campana.getPromocion().getIdPromocion())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Promoción no encontrada: " + campana.getPromocion().getIdPromocion()));
            campana.setPromocion(promo);
            if (campana.getAsunto() == null || campana.getAsunto().isBlank()) {
                campana.setAsunto("Promoción: " + promo.getTitulo());
            }
        }
    }

    private String construirCuerpo(Campana campana) {
        String base = campana.getCuerpoMensaje() != null ? campana.getCuerpoMensaje() : "";
        if (campana.getPromocion() != null) {
            Promocion p = campana.getPromocion();
            base += "\n\nPromoción: " + p.getTitulo();
            if (p.getPorcentajeDescuento() != null) {
                base += " — Descuento " + p.getPorcentajeDescuento() + "%";
            }
            if (p.getCodigo() != null && !p.getCodigo().isBlank()) {
                base += "\nCódigo: " + p.getCodigo();
            }
        }
        return base.trim();
    }

    private String buscarTelefono(Usuario usuario) {
        return perfilRepository.findAll().stream()
                .filter(p -> p.getUsuario() != null
                        && usuario.getIdUsuario().equals(p.getUsuario().getIdUsuario()))
                .map(PerfilCliente::getTelefono)
                .findFirst()
                .orElse(null);
    }

    private List<Usuario> resolverDestinatarios(String segmento) {
        Set<Integer> ids = new HashSet<>();
        List<Usuario> resultado = new ArrayList<>();

        if (segmento == null || "TODOS".equals(segmento)) {
            usuarioRepository.findAll().stream()
                    .filter(u -> "CLIENTE".equals(u.getRol()))
                    .forEach(u -> agregarSiNuevo(resultado, ids, u));
            return resultado;
        }

        if ("MINORISTA".equals(segmento) || "MAYORISTA".equals(segmento)) {
            for (PerfilCliente perfil : perfilRepository.findAll()) {
                if (segmento.equals(perfil.getTipoCliente())
                        && perfil.getUsuario() != null
                        && "CLIENTE".equals(perfil.getUsuario().getRol())) {
                    agregarSiNuevo(resultado, ids, perfil.getUsuario());
                }
            }
            return resultado;
        }

        if ("CLIENTES_ACTIVOS".equals(segmento)) {
            pedidoRepository.findAll().forEach(p -> {
                if (p.getUsuario() != null && "CLIENTE".equals(p.getUsuario().getRol())) {
                    agregarSiNuevo(resultado, ids, p.getUsuario());
                }
            });
            return resultado;
        }

        if ("CON_DEUDA".equals(segmento)) {
            pedidoRepository.findAll().forEach(p -> {
                String estado = p.getEstado();
                if (p.getUsuario() != null
                        && "CLIENTE".equals(p.getUsuario().getRol())
                        && ("PENDIENTE".equals(estado) || "PARCIAL".equals(estado))) {
                    agregarSiNuevo(resultado, ids, p.getUsuario());
                }
            });
            return resultado;
        }

        if ("MOROSOS".equals(segmento)) {
            cuotaRepository.findByEstado("VENCIDA").forEach(c -> {
                if (c.getPlan() != null
                        && c.getPlan().getCliente() != null
                        && c.getPlan().getCliente().getUsuario() != null) {
                    Usuario u = c.getPlan().getCliente().getUsuario();
                    if ("CLIENTE".equals(u.getRol())) {
                        agregarSiNuevo(resultado, ids, u);
                    }
                }
            });
            return resultado;
        }

        return resultado;
    }

    private void agregarSiNuevo(List<Usuario> lista, Set<Integer> ids, Usuario u) {
        if (u.getIdUsuario() != null && ids.add(u.getIdUsuario())) {
            lista.add(u);
        }
    }
}
