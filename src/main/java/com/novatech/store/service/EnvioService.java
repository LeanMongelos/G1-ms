package com.novatech.store.service;

import com.novatech.store.dto.EnvioDetalleResponse;
import com.novatech.store.entity.Envio;
import com.novatech.store.exception.ResourceNotFoundException;
import com.novatech.store.repository.EnvioRepository;
import com.novatech.store.repository.FacturaRepository;
import com.novatech.store.repository.PedidoRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Logica de negocio para los envios.
/**
 * Servicio `EnvioService`: reglas de negocio, transacciones y orquestación de Envio. Los controllers delegan aquí; no accede HTTP directamente.
 */
@Service
public class EnvioService {

    private final EnvioRepository repository;
    private final PedidoRepository pedidoRepository;
    private final PedidoService pedidoService;
    private final FacturaRepository facturaRepository;

    public EnvioService(EnvioRepository repository,
                        PedidoRepository pedidoRepository,
                        PedidoService pedidoService,
                        FacturaRepository facturaRepository) {
        this.repository = repository;
        this.pedidoRepository = pedidoRepository;
        this.pedidoService = pedidoService;
        this.facturaRepository = facturaRepository;
    }

    // Trae todos los envios.
    public List<Envio> listar() {
        return repository.findAll();
    }

    // Trae un envio por su id. Si no existe, lanza error 404.
    public Envio obtener(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Envio no encontrado: " + id));
    }

    public EnvioDetalleResponse obtenerDetalle(Integer idEnvio) {
        Envio envio = obtener(idEnvio);
        if (envio.getPedido() == null || envio.getPedido().getIdPedido() == null) {
            EnvioDetalleResponse vacio = new EnvioDetalleResponse();
            vacio.setEnvio(envio);
            return vacio;
        }
        var pedidoDet = pedidoService.obtenerDetalle(envio.getPedido().getIdPedido());
        EnvioDetalleResponse r = new EnvioDetalleResponse();
        r.setEnvio(envio);
        r.setPedido(pedidoDet.getPedido());
        if (pedidoDet.getPedido() != null && pedidoDet.getPedido().getUsuario() != null) {
            r.setClienteNombre(pedidoDet.getPedido().getUsuario().getNombre());
            r.setClienteEmail(pedidoDet.getPedido().getUsuario().getEmail());
        }
        r.setLineas(pedidoDet.getDetalles());
        r.setPagos(pedidoDet.getPagos());
        r.setSaldoPendiente(pedidoDet.getSaldoPendiente());
        if (pedidoDet.getIdFactura() != null) {
            facturaRepository.findById(pedidoDet.getIdFactura()).ifPresent(r::setFactura);
        }
        return r;
    }

    // Crea un envio nuevo. Si no viene estado, lo dejamos "PREPARANDO".
    public Envio crear(Envio envio) {
        envio.setIdEnvio(null);
        if (envio.getEstadoEnvio() == null || envio.getEstadoEnvio().isBlank()) {
            envio.setEstadoEnvio("PREPARANDO");
        }
        return repository.save(envio);
    }

    // Actualiza un envio existente y sincroniza el estado del pedido si corresponde.
    @Transactional
    public Envio actualizar(Integer id, Envio datos) {
        Envio envio = obtener(id);
        envio.setPedido(datos.getPedido());
        envio.setDireccionEnvio(datos.getDireccionEnvio());
        envio.setEmpresaLogistica(datos.getEmpresaLogistica());
        envio.setEstadoEnvio(normalizarEstadoEnvio(datos.getEstadoEnvio()));
        envio.setNumeroTracking(datos.getNumeroTracking());
        envio.setCostoEnvio(datos.getCostoEnvio());
        if (datos.getFechaDespacho() != null) {
            envio.setFechaDespacho(datos.getFechaDespacho());
        } else if ("EN_CAMINO".equalsIgnoreCase(envio.getEstadoEnvio())
                && envio.getFechaDespacho() == null) {
            envio.setFechaDespacho(LocalDateTime.now());
        }

        Envio guardado = repository.save(envio);
        sincronizarEstadoPedido(guardado);
        return guardado;
    }

    private void sincronizarEstadoPedido(Envio envio) {
        if (envio.getPedido() == null || envio.getPedido().getIdPedido() == null) {
            return;
        }
        String estadoEnvio = normalizarEstadoEnvio(envio.getEstadoEnvio());
        if (estadoEnvio == null) {
            return;
        }

        pedidoRepository.findById(envio.getPedido().getIdPedido()).ifPresent(pedido -> {
            if ("EN_CAMINO".equalsIgnoreCase(estadoEnvio)) {
                pedido.setEstado("EN_CAMINO");
                pedidoRepository.save(pedido);
            } else if ("ENTREGADO".equalsIgnoreCase(estadoEnvio)) {
                pedido.setEstado("ENTREGADO");
                pedidoRepository.save(pedido);
            }
        });
    }

    private String normalizarEstadoEnvio(String estado) {
        if (estado == null || estado.isBlank()) {
            return estado;
        }
        return estado.trim().toUpperCase().replace(' ', '_');
    }

    // Borra un envio por su id.
    public void eliminar(Integer id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Envio no encontrado: " + id);
        }
        repository.deleteById(id);
    }
}
