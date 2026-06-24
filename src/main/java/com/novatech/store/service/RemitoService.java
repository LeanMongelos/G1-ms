package com.novatech.store.service;

import com.novatech.store.dto.LineaComprobanteDto;
import com.novatech.store.dto.RemitoRequest;
import com.novatech.store.entity.DetallePedido;
import com.novatech.store.entity.DetallePresupuesto;
import com.novatech.store.entity.DetalleRemito;
import com.novatech.store.entity.Pedido;
import com.novatech.store.entity.Presupuesto;
import com.novatech.store.entity.Remito;
import com.novatech.store.exception.ReglaNegocioException;
import com.novatech.store.exception.ResourceNotFoundException;
import com.novatech.store.repository.DetallePedidoRepository;
import com.novatech.store.repository.PedidoRepository;
import com.novatech.store.repository.PerfilClienteRepository;
import com.novatech.store.repository.PresupuestoRepository;
import com.novatech.store.repository.ProductoRepository;
import com.novatech.store.repository.RemitoRepository;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio `RemitoService`: reglas de negocio, transacciones y orquestación de Remito. Los controllers delegan aquí; no accede HTTP directamente.
 */
@Service
public class RemitoService {

    private final RemitoRepository repository;
    private final PedidoRepository pedidoRepository;
    private final PresupuestoRepository presupuestoRepository;
    private final PerfilClienteRepository perfilRepository;
    private final ProductoRepository productoRepository;
    private final DetallePedidoRepository detallePedidoRepository;

    public RemitoService(RemitoRepository repository,
                         PedidoRepository pedidoRepository,
                         PresupuestoRepository presupuestoRepository,
                         PerfilClienteRepository perfilRepository,
                         ProductoRepository productoRepository,
                         DetallePedidoRepository detallePedidoRepository) {
        this.repository = repository;
        this.pedidoRepository = pedidoRepository;
        this.presupuestoRepository = presupuestoRepository;
        this.perfilRepository = perfilRepository;
        this.productoRepository = productoRepository;
        this.detallePedidoRepository = detallePedidoRepository;
    }

    public List<Remito> listar() {
        return repository.findAll();
    }

    public Remito obtener(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Remito no encontrado: " + id));
    }

    @Transactional
    public Remito crear(RemitoRequest request) {
        if (request.getLineas() == null || request.getLineas().isEmpty()) {
            throw new ReglaNegocioException("Agregá al menos una línea al remito.");
        }
        Remito remito = new Remito();
        remito.setNumeroRemito(generarNumero());
        remito.setFecha(LocalDateTime.now());
        remito.setEstado("PREPARADO");
        remito.setDireccionEntrega(request.getDireccionEntrega());
        remito.setNotas(request.getNotas());
        vincularReferencias(remito, request);
        aplicarLineas(remito, request.getLineas());
        return repository.save(remito);
    }

    @Transactional
    public Remito generarDesdePedido(Integer pedidoId, String direccionEntrega) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado: " + pedidoId));
        List<DetallePedido> detalles = detallePedidoRepository.findByPedidoIdPedido(pedidoId);
        if (detalles.isEmpty()) {
            throw new ReglaNegocioException("El pedido no tiene líneas para remitir.");
        }

        RemitoRequest request = new RemitoRequest();
        request.setPedidoId(pedidoId);
        request.setDireccionEntrega(direccionEntrega);
        if (pedido.getUsuario() != null) {
            perfilRepository.findByUsuario_IdUsuario(pedido.getUsuario().getIdUsuario())
                    .ifPresent(c -> request.setIdCliente(c.getIdCliente()));
        }
        request.setLineas(detalles.stream().map(d -> {
            LineaComprobanteDto dto = new LineaComprobanteDto();
            dto.setIdProducto(d.getProducto().getIdProducto());
            dto.setCantidad(d.getCantidad());
            dto.setDescripcion(d.getProducto().getNombre());
            return dto;
        }).toList());
        return crear(request);
    }

    @Transactional
    public Remito generarDesdePresupuesto(Integer presupuestoId, String direccionEntrega) {
        Presupuesto presupuesto = presupuestoRepository.findById(presupuestoId)
                .orElseThrow(() -> new ResourceNotFoundException("Presupuesto no encontrado: " + presupuestoId));
        if (presupuesto.getLineas() == null || presupuesto.getLineas().isEmpty()) {
            throw new ReglaNegocioException("El presupuesto no tiene líneas para remitir.");
        }

        RemitoRequest request = new RemitoRequest();
        request.setPresupuestoId(presupuestoId);
        request.setDireccionEntrega(direccionEntrega != null ? direccionEntrega
                : (presupuesto.getCliente() != null ? presupuesto.getCliente().getDireccion() : null));
        if (presupuesto.getCliente() != null) {
            request.setIdCliente(presupuesto.getCliente().getIdCliente());
        }
        request.setLineas(presupuesto.getLineas().stream().map(l -> {
            LineaComprobanteDto dto = new LineaComprobanteDto();
            dto.setIdProducto(l.getProducto().getIdProducto());
            dto.setCantidad(l.getCantidad());
            dto.setDescripcion(l.getProducto().getNombre());
            return dto;
        }).toList());
        return crear(request);
    }

    @Transactional
    public Remito cambiarEstado(Integer id, String nuevoEstado) {
        Remito remito = obtener(id);
        String estado = nuevoEstado != null ? nuevoEstado.toUpperCase() : "";
        if (!List.of("PREPARADO", "DESPACHADO", "ENTREGADO").contains(estado)) {
            throw new ReglaNegocioException("Estado de remito no válido: " + nuevoEstado);
        }
        remito.setEstado(estado);
        return repository.save(remito);
    }

    @Transactional
    public Remito actualizar(Integer id, RemitoRequest request) {
        Remito remito = obtener(id);
        if ("ENTREGADO".equalsIgnoreCase(remito.getEstado())) {
            throw new ReglaNegocioException("No se puede editar un remito entregado.");
        }
        if (request.getDireccionEntrega() != null) {
            remito.setDireccionEntrega(request.getDireccionEntrega());
        }
        remito.setNotas(request.getNotas());
        if (request.getLineas() != null && !request.getLineas().isEmpty()) {
            aplicarLineas(remito, request.getLineas());
        }
        return repository.save(remito);
    }

    public void eliminar(Integer id) {
        Remito remito = obtener(id);
        if ("ENTREGADO".equalsIgnoreCase(remito.getEstado())) {
            throw new ReglaNegocioException("No se puede eliminar un remito entregado.");
        }
        repository.delete(remito);
    }

    private void vincularReferencias(Remito remito, RemitoRequest request) {
        if (request.getPedidoId() != null) {
            remito.setPedido(pedidoRepository.findById(request.getPedidoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado: " + request.getPedidoId())));
        }
        if (request.getPresupuestoId() != null) {
            remito.setPresupuesto(presupuestoRepository.findById(request.getPresupuestoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Presupuesto no encontrado: " + request.getPresupuestoId())));
        }
        if (request.getIdCliente() != null) {
            remito.setCliente(perfilRepository.findById(request.getIdCliente())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado: " + request.getIdCliente())));
        } else if (remito.getPedido() != null && remito.getPedido().getUsuario() != null) {
            perfilRepository.findByUsuario_IdUsuario(remito.getPedido().getUsuario().getIdUsuario())
                    .ifPresent(remito::setCliente);
        } else if (remito.getPresupuesto() != null && remito.getPresupuesto().getCliente() != null) {
            remito.setCliente(remito.getPresupuesto().getCliente());
        }
    }

    private void aplicarLineas(Remito remito, List<LineaComprobanteDto> lineasDto) {
        remito.getLineas().clear();
        List<DetalleRemito> lineas = new ArrayList<>();
        for (LineaComprobanteDto dto : lineasDto) {
            if (dto.getIdProducto() == null || dto.getCantidad() == null || dto.getCantidad() < 1) {
                throw new ReglaNegocioException("Cada línea debe tener producto y cantidad válida.");
            }
            var producto = productoRepository.findById(dto.getIdProducto())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + dto.getIdProducto()));
            DetalleRemito det = new DetalleRemito();
            det.setRemito(remito);
            det.setProducto(producto);
            det.setCantidad(dto.getCantidad());
            det.setDescripcion(dto.getDescripcion() != null ? dto.getDescripcion() : producto.getNombre());
            lineas.add(det);
        }
        remito.getLineas().addAll(lineas);
    }

    private String generarNumero() {
        long count = repository.count() + 1;
        return "REM-" + Year.now().getValue() + "-" + String.format("%06d", count);
    }
}
