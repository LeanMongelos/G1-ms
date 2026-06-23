package com.novatech.store.service;

import com.novatech.store.dto.ActualizarPerfilClienteRequest;
import com.novatech.store.dto.ActualizarPerfilRequest;
import com.novatech.store.dto.CrearTicketClienteRequest;
import com.novatech.store.dto.EnviarMensajeTicketRequest;
import com.novatech.store.dto.CuotaClienteItemDto;
import com.novatech.store.dto.CuotaResumenDto;
import com.novatech.store.dto.PrestamoClienteDto;
import com.novatech.store.dto.PedidoDetalleResponse;
import com.novatech.store.dto.UsuarioResponse;
import com.novatech.store.entity.Conversacion;
import com.novatech.store.entity.Cuota;
import com.novatech.store.entity.Factura;
import com.novatech.store.entity.MensajeConversacion;
import com.novatech.store.entity.Pedido;
import com.novatech.store.entity.PerfilCliente;
import com.novatech.store.entity.PlanCuotas;
import com.novatech.store.entity.SolicitudDevolucion;
import com.novatech.store.exception.AccesoDenegadoException;
import com.novatech.store.exception.ReglaNegocioException;
import com.novatech.store.exception.ResourceNotFoundException;
import com.novatech.store.repository.ConversacionRepository;
import com.novatech.store.repository.CuotaRepository;
import com.novatech.store.repository.FacturaRepository;
import com.novatech.store.repository.MensajeConversacionRepository;
import com.novatech.store.repository.PedidoRepository;
import com.novatech.store.repository.PerfilClienteRepository;
import com.novatech.store.security.SecurityUtils;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClientePortalService {

    private final PedidoService pedidoService;
    private final PedidoRepository pedidoRepository;
    private final FacturaRepository facturaRepository;
    private final PerfilClienteRepository perfilRepository;
    private final UsuarioService usuarioService;
    private final ConversacionRepository conversacionRepository;
    private final MensajeConversacionRepository mensajeRepository;
    private final SolicitudDevolucionService devolucionService;
    private final CuotaRepository cuotaRepository;
    private final CuotaService cuotaService;

    public ClientePortalService(PedidoService pedidoService,
                                PedidoRepository pedidoRepository,
                                FacturaRepository facturaRepository,
                                PerfilClienteRepository perfilRepository,
                                UsuarioService usuarioService,
                                ConversacionRepository conversacionRepository,
                                MensajeConversacionRepository mensajeRepository,
                                SolicitudDevolucionService devolucionService,
                                CuotaRepository cuotaRepository,
                                CuotaService cuotaService) {
        this.pedidoService = pedidoService;
        this.pedidoRepository = pedidoRepository;
        this.facturaRepository = facturaRepository;
        this.perfilRepository = perfilRepository;
        this.usuarioService = usuarioService;
        this.conversacionRepository = conversacionRepository;
        this.mensajeRepository = mensajeRepository;
        this.devolucionService = devolucionService;
        this.cuotaRepository = cuotaRepository;
        this.cuotaService = cuotaService;
    }

    private UsuarioResponse usuarioActual() {
        SecurityUtils.requerirCliente();
        return SecurityUtils.requerirAutenticado();
    }

    public List<Pedido> listarPedidos() {
        UsuarioResponse u = usuarioActual();
        return pedidoRepository.findByUsuario_IdUsuarioOrderByFechaDesc(u.idUsuario()).stream()
                .sorted(Comparator
                        .comparing(Pedido::getFecha, Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(Pedido::getIdPedido, Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
    }

    public PedidoDetalleResponse obtenerPedido(Integer id) {
        UsuarioResponse u = usuarioActual();
        Pedido pedido = pedidoService.obtener(id);
        verificarPedidoPropio(pedido, u.idUsuario());
        return pedidoService.obtenerDetalle(id);
    }

    public List<Factura> listarFacturas() {
        UsuarioResponse u = usuarioActual();
        return facturaRepository.findByPedidoUsuarioOrderByFechaEmisionDesc(u.idUsuario());
    }

    public Factura obtenerFactura(Integer id) {
        UsuarioResponse u = usuarioActual();
        Factura factura = facturaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Factura no encontrada: " + id));
        verificarFacturaPropia(factura, u.idUsuario());
        return factura;
    }

    public PerfilCliente obtenerPerfil() {
        UsuarioResponse u = usuarioActual();
        return perfilRepository.findByUsuario_IdUsuario(u.idUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Perfil de cliente no encontrado"));
    }

    @Transactional
    public PerfilCliente actualizarPerfil(ActualizarPerfilClienteRequest datos) {
        UsuarioResponse u = usuarioActual();
        PerfilCliente perfil = perfilRepository.findByUsuario_IdUsuario(u.idUsuario())
                .orElseGet(() -> crearPerfilBasico(u));

        if (datos.direccion() != null) {
            perfil.setDireccion(datos.direccion());
        }
        if (datos.ciudad() != null) {
            perfil.setCiudad(datos.ciudad());
        }
        if (datos.telefono() != null) {
            perfil.setTelefono(datos.telefono());
        }

        if (datos.nombre() != null || datos.email() != null) {
            String nombre = datos.nombre() != null ? datos.nombre() : u.nombre();
            String email = datos.email() != null ? datos.email() : u.email();
            if (nombre.isBlank() || email.isBlank()) {
                throw new ReglaNegocioException("Nombre y email no pueden quedar vacíos.");
            }
            usuarioService.actualizarPerfil(u.idUsuario(), new ActualizarPerfilRequest(nombre, email, null));
        }

        return perfilRepository.save(perfil);
    }

    public List<Conversacion> listarTickets() {
        UsuarioResponse u = usuarioActual();
        return conversacionRepository.findByClienteUsuarioOrEmail(u.idUsuario(), u.email());
    }

    @Transactional
    public Conversacion crearTicket(CrearTicketClienteRequest request) {
        UsuarioResponse u = usuarioActual();
        LocalDateTime ahora = LocalDateTime.now();

        PerfilCliente perfil = perfilRepository.findByUsuario_IdUsuario(u.idUsuario()).orElse(null);

        Pedido pedido = null;
        if (request.idPedido() != null) {
            pedido = pedidoRepository.findById(request.idPedido())
                    .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado: " + request.idPedido()));
            verificarPedidoPropio(pedido, u.idUsuario());
        }

        Conversacion conv = new Conversacion();
        conv.setCanal("WEB");
        conv.setAsunto(request.asunto());
        conv.setContactoNombre(u.nombre());
        conv.setContactoEmail(u.email());
        conv.setEstado("PENDIENTE");
        conv.setEtiquetas(request.etiquetas());
        conv.setCliente(perfil);
        conv.setPedido(pedido);
        conv.setVistaPrevia(truncar(request.cuerpo(), 120));
        conv.setFechaCreacion(ahora);
        conv.setUltimaActividad(ahora);
        conv = conversacionRepository.save(conv);

        MensajeConversacion mensaje = new MensajeConversacion();
        mensaje.setConversacion(conv);
        mensaje.setDireccion("ENTRANTE");
        mensaje.setCuerpo(request.cuerpo());
        mensaje.setRemitenteNombre(u.nombre());
        mensaje.setFecha(ahora);
        mensajeRepository.save(mensaje);

        return conv;
    }

    public List<MensajeConversacion> listarMensajesTicket(Integer id) {
        UsuarioResponse u = usuarioActual();
        Conversacion conv = obtenerTicketPropio(id, u);
        return mensajeRepository.findByConversacionIdConversacionOrderByFechaAsc(conv.getIdConversacion());
    }

    @Transactional
    public MensajeConversacion responderTicket(Integer id, EnviarMensajeTicketRequest request) {
        UsuarioResponse u = usuarioActual();
        Conversacion conv = obtenerTicketPropio(id, u);
        LocalDateTime ahora = LocalDateTime.now();

        MensajeConversacion mensaje = new MensajeConversacion();
        mensaje.setConversacion(conv);
        mensaje.setDireccion("ENTRANTE");
        mensaje.setCuerpo(request.cuerpo());
        mensaje.setRemitenteNombre(u.nombre());
        mensaje.setFecha(ahora);
        MensajeConversacion guardado = mensajeRepository.save(mensaje);

        conv.setVistaPrevia(truncar(request.cuerpo(), 120));
        conv.setUltimaActividad(ahora);
        if ("RESUELTA".equalsIgnoreCase(conv.getEstado())) {
            conv.setEstado("PENDIENTE");
        }
        conversacionRepository.save(conv);
        return guardado;
    }

    public List<SolicitudDevolucion> listarDevoluciones() {
        return devolucionService.listarPorUsuario(usuarioActual().idUsuario());
    }

    public SolicitudDevolucion crearDevolucion(com.novatech.store.dto.CrearDevolucionRequest request) {
        return devolucionService.crear(request, usuarioActual());
    }

    public SolicitudDevolucion obtenerDevolucion(Integer id) {
        return devolucionService.obtener(id, usuarioActual().idUsuario());
    }

    public List<CuotaResumenDto> listarCuotas() {
        return cuotaRepository.findByUsuario(usuarioActual().idUsuario()).stream()
                .map(CuotaResumenDto::desde)
                .toList();
    }

    public List<PrestamoClienteDto> listarPrestamos() {
        cuotaService.actualizarVencidas();
        List<Cuota> todas = cuotaRepository.findByUsuario(usuarioActual().idUsuario());
        Map<Integer, List<Cuota>> porPlan = todas.stream()
                .filter(c -> c.getPlan() != null && c.getPlan().getIdPlan() != null)
                .collect(Collectors.groupingBy(c -> c.getPlan().getIdPlan()));

        return porPlan.values().stream()
                .map(this::mapearPrestamo)
                .sorted(Comparator.comparing(PrestamoClienteDto::idPlan, Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
    }

    private PrestamoClienteDto mapearPrestamo(List<Cuota> cuotasPlan) {
        cuotasPlan.sort(Comparator.comparing(Cuota::getNumeroCuota, Comparator.nullsLast(Comparator.naturalOrder())));
        PlanCuotas plan = cuotasPlan.get(0).getPlan();

        List<CuotaClienteItemDto> items = cuotasPlan.stream().map(CuotaClienteItemDto::desde).toList();

        int pagadas = (int) cuotasPlan.stream()
                .filter(c -> "PAGADA".equalsIgnoreCase(c.getEstado()))
                .count();

        Cuota proxima = cuotasPlan.stream()
                .filter(c -> !"PAGADA".equalsIgnoreCase(c.getEstado()))
                .min(Comparator.comparing(Cuota::getNumeroCuota, Comparator.nullsLast(Comparator.naturalOrder())))
                .orElse(null);

        java.math.BigDecimal saldoPendiente = cuotasPlan.stream()
                .filter(c -> !"PAGADA".equalsIgnoreCase(c.getEstado()))
                .map(Cuota::getMonto)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

        java.math.BigDecimal totalFinanciado = cuotasPlan.stream()
                .map(Cuota::getMonto)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

        Integer idPedido = plan.getPedido() != null ? plan.getPedido().getIdPedido() : null;

        return new PrestamoClienteDto(
                plan.getIdPlan(),
                idPedido,
                plan.getCantidadCuotas(),
                plan.getInteres(),
                plan.getEstado(),
                totalFinanciado,
                pagadas,
                proxima != null ? proxima.getNumeroCuota() : null,
                proxima != null ? proxima.getIdCuota() : null,
                saldoPendiente,
                proxima != null ? proxima.getMonto() : null,
                proxima != null ? proxima.getFechaVencimiento() : null,
                items);
    }

    private PerfilCliente crearPerfilBasico(UsuarioResponse u) {
        PerfilCliente perfil = new PerfilCliente();
        perfil.setUsuario(usuarioService.obtener(u.idUsuario()));
        perfil.setActivo(true);
        perfil.setCreadoEn(LocalDateTime.now());
        return perfilRepository.save(perfil);
    }

    private Conversacion obtenerTicketPropio(Integer id, UsuarioResponse u) {
        Conversacion conv = conversacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket no encontrado: " + id));
        if (conv.getCliente() != null
                && conv.getCliente().getUsuario() != null
                && u.idUsuario().equals(conv.getCliente().getUsuario().getIdUsuario())) {
            return conv;
        }
        if (conv.getContactoEmail() != null
                && u.email() != null
                && conv.getContactoEmail().equalsIgnoreCase(u.email())) {
            return conv;
        }
        throw new AccesoDenegadoException("No podés acceder a este ticket");
    }

    private void verificarPedidoPropio(Pedido pedido, Integer idUsuario) {
        if (pedido.getUsuario() == null || !idUsuario.equals(pedido.getUsuario().getIdUsuario())) {
            throw new AccesoDenegadoException("No podés acceder a este pedido");
        }
    }

    private void verificarFacturaPropia(Factura factura, Integer idUsuario) {
        if (factura.getPedido() == null
                || factura.getPedido().getUsuario() == null
                || !idUsuario.equals(factura.getPedido().getUsuario().getIdUsuario())) {
            throw new AccesoDenegadoException("No podés acceder a esta factura");
        }
    }

    private String truncar(String texto, int max) {
        if (texto == null) {
            return "";
        }
        return texto.length() <= max ? texto : texto.substring(0, max) + "…";
    }
}
