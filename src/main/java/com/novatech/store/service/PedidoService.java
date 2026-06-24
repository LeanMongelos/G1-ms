package com.novatech.store.service;

import com.novatech.store.dto.PedidoDetalleResponse;
import com.novatech.store.entity.DetallePedido;
import com.novatech.store.entity.Envio;
import com.novatech.store.entity.Factura;
import com.novatech.store.entity.Pago;
import com.novatech.store.entity.Pedido;
import com.novatech.store.entity.PlanCuotas;
import com.novatech.store.exception.ResourceNotFoundException;
import com.novatech.store.repository.DetallePedidoRepository;
import com.novatech.store.repository.EnvioRepository;
import com.novatech.store.repository.FacturaRepository;
import com.novatech.store.repository.PagoRepository;
import com.novatech.store.repository.PedidoRepository;
import com.novatech.store.repository.PlanCuotasRepository;
import com.novatech.store.util.PagoUtil;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

// Logica de negocio para los pedidos.
/**
 * Servicio `PedidoService`: reglas de negocio, transacciones y orquestación de Pedido. Los controllers delegan aquí; no accede HTTP directamente.
 */
@Service
public class PedidoService {

    private final PedidoRepository repository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final PagoRepository pagoRepository;
    private final EnvioRepository envioRepository;
    private final PlanCuotasRepository planCuotasRepository;
    private final FacturaRepository facturaRepository;

    public PedidoService(PedidoRepository repository,
                         DetallePedidoRepository detallePedidoRepository,
                         PagoRepository pagoRepository,
                         EnvioRepository envioRepository,
                         PlanCuotasRepository planCuotasRepository,
                         FacturaRepository facturaRepository) {
        this.repository = repository;
        this.detallePedidoRepository = detallePedidoRepository;
        this.pagoRepository = pagoRepository;
        this.envioRepository = envioRepository;
        this.planCuotasRepository = planCuotasRepository;
        this.facturaRepository = facturaRepository;
    }

    // Trae todos los pedidos, con filtros opcionales.
    public List<Pedido> listar(String canalOrigen, String estado) {
        return repository.findAll().stream()
                .filter(p -> canalOrigen == null || canalOrigen.isBlank()
                        || canalOrigen.equalsIgnoreCase(p.getCanalOrigen()))
                .filter(p -> estado == null || estado.isBlank()
                        || estado.equalsIgnoreCase(p.getEstado()))
                .toList();
    }

    public List<Pedido> listarPorUsuario(Integer idUsuario) {
        return repository.findByUsuario_IdUsuarioOrderByFechaDesc(idUsuario);
    }

    // Trae un pedido por su id. Si no existe, lanza error 404.
    public Pedido obtener(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado: " + id));
    }

    // Trae el pedido con detalles, pagos, envio y saldo pendiente.
    public PedidoDetalleResponse obtenerDetalle(Integer id) {
        Pedido pedido = obtener(id);
        List<DetallePedido> detalles = detallePedidoRepository.findByPedidoIdPedido(id);
        List<Pago> pagos = pagoRepository.findByPedidoIdPedido(id);
        List<Envio> envios = envioRepository.findByPedidoIdPedido(id);
        List<PlanCuotas> planes = planCuotasRepository.findByPedidoIdPedido(id);
        List<Factura> facturas = facturaRepository.findByPedidoIdPedido(id);

        BigDecimal total = pedido.getTotal() == null ? BigDecimal.ZERO : pedido.getTotal();
        BigDecimal saldo = PagoUtil.saldoPedido(total, pagos);

        PedidoDetalleResponse response = new PedidoDetalleResponse();
        response.setPedido(pedido);
        response.setDetalles(detalles);
        response.setPagos(pagos);
        response.setEnvio(envios.isEmpty() ? null : envios.get(0));
        response.setPlanCuotas(planes.isEmpty() ? null : planes.get(0));
        response.setSaldoPendiente(saldo);
        response.setIdFactura(facturas.isEmpty() ? null : facturas.get(0).getIdFactura());
        return response;
    }

    // Crea un pedido nuevo. Ponemos la fecha de ahora y, si no viene estado, "PENDIENTE".
    public Pedido crear(Pedido pedido) {
        pedido.setIdPedido(null);
        if (pedido.getFecha() == null) {
            pedido.setFecha(LocalDateTime.now());
        }
        if (pedido.getEstado() == null || pedido.getEstado().isBlank()) {
            pedido.setEstado("PENDIENTE");
        }
        return repository.save(pedido);
    }

    // Actualiza un pedido existente.
    public Pedido actualizar(Integer id, Pedido datos) {
        Pedido pedido = obtener(id);
        pedido.setUsuario(datos.getUsuario());
        pedido.setEstado(datos.getEstado());
        pedido.setTotal(datos.getTotal());
        pedido.setCanalOrigen(datos.getCanalOrigen());
        pedido.setTipoEntrega(datos.getTipoEntrega());
        pedido.setNotas(datos.getNotas());
        if (datos.getFecha() != null) {
            pedido.setFecha(datos.getFecha());
        }
        return repository.save(pedido);
    }

    // Borra un pedido por su id.
    public void eliminar(Integer id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Pedido no encontrado: " + id);
        }
        repository.deleteById(id);
    }
}
