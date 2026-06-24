package com.novatech.store.service;

import com.novatech.store.dto.CrearDevolucionRequest;
import com.novatech.store.dto.UsuarioResponse;
import com.novatech.store.entity.Pedido;
import com.novatech.store.entity.PerfilCliente;
import com.novatech.store.entity.SolicitudDevolucion;
import com.novatech.store.exception.AccesoDenegadoException;
import com.novatech.store.exception.ReglaNegocioException;
import com.novatech.store.exception.ResourceNotFoundException;
import com.novatech.store.repository.PedidoRepository;
import com.novatech.store.repository.PerfilClienteRepository;
import com.novatech.store.repository.SolicitudDevolucionRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio `SolicitudDevolucionService`: reglas de negocio, transacciones y orquestación de SolicitudDevolucion. Los controllers delegan aquí; no accede HTTP directamente.
 */
@Service
public class SolicitudDevolucionService {

    private static final Set<String> ESTADOS_PEDIDO_DEVOLUCION = Set.of(
            "ENTREGADO", "COMPLETADO", "PAGADO", "ENVIADO");

    private final SolicitudDevolucionRepository repository;
    private final PedidoRepository pedidoRepository;
    private final PerfilClienteRepository perfilRepository;

    public SolicitudDevolucionService(SolicitudDevolucionRepository repository,
                                        PedidoRepository pedidoRepository,
                                        PerfilClienteRepository perfilRepository) {
        this.repository = repository;
        this.pedidoRepository = pedidoRepository;
        this.perfilRepository = perfilRepository;
    }

    public List<SolicitudDevolucion> listarPorUsuario(Integer idUsuario) {
        return repository.findByCliente_Usuario_IdUsuarioOrderByFechaCreacionDesc(idUsuario);
    }

    public SolicitudDevolucion obtener(Integer id, Integer idUsuario) {
        SolicitudDevolucion solicitud = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud de devolución no encontrada: " + id));
        verificarPropiedad(solicitud, idUsuario);
        return solicitud;
    }

    @Transactional
    public SolicitudDevolucion crear(CrearDevolucionRequest request, UsuarioResponse usuario) {
        Pedido pedido = pedidoRepository.findById(request.idPedido())
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado: " + request.idPedido()));

        if (pedido.getUsuario() == null
                || !usuario.idUsuario().equals(pedido.getUsuario().getIdUsuario())) {
            throw new AccesoDenegadoException("No podés solicitar devolución de un pedido que no es tuyo");
        }

        String estado = pedido.getEstado() != null ? pedido.getEstado().toUpperCase() : "";
        if (!ESTADOS_PEDIDO_DEVOLUCION.contains(estado)) {
            throw new ReglaNegocioException(
                    "Solo se pueden devolver pedidos entregados o completados (estado actual: "
                            + pedido.getEstado() + ").");
        }

        PerfilCliente perfil = perfilRepository.findByUsuario_IdUsuario(usuario.idUsuario())
                .orElse(null);

        LocalDateTime ahora = LocalDateTime.now();
        SolicitudDevolucion solicitud = new SolicitudDevolucion();
        solicitud.setPedido(pedido);
        solicitud.setCliente(perfil);
        solicitud.setMotivo(request.motivo());
        solicitud.setDescripcion(request.descripcion());
        solicitud.setLineasJson(request.lineasJson());
        solicitud.setEstado("SOLICITADA");
        solicitud.setFechaCreacion(ahora);
        solicitud.setFechaActualizacion(ahora);
        return repository.save(solicitud);
    }

    private void verificarPropiedad(SolicitudDevolucion solicitud, Integer idUsuario) {
        if (solicitud.getCliente() != null
                && solicitud.getCliente().getUsuario() != null
                && idUsuario.equals(solicitud.getCliente().getUsuario().getIdUsuario())) {
            return;
        }
        if (solicitud.getPedido() != null
                && solicitud.getPedido().getUsuario() != null
                && idUsuario.equals(solicitud.getPedido().getUsuario().getIdUsuario())) {
            return;
        }
        throw new AccesoDenegadoException("No podés acceder a esta solicitud de devolución");
    }
}
