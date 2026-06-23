package com.novatech.store.service;

import com.novatech.store.dto.DashboardKpiResponse;
import com.novatech.store.dto.EstadoCantidadDto;
import com.novatech.store.dto.FacturaRecienteDto;
import com.novatech.store.dto.PedidoRecienteDto;
import com.novatech.store.entity.Factura;
import com.novatech.store.entity.Pago;
import com.novatech.store.entity.Pedido;
import com.novatech.store.entity.Producto;
import com.novatech.store.repository.CampanaRepository;
import com.novatech.store.repository.ConversacionRepository;
import com.novatech.store.repository.CuotaRepository;
import com.novatech.store.repository.FacturaRepository;
import com.novatech.store.repository.PagoRepository;
import com.novatech.store.repository.PedidoRepository;
import com.novatech.store.repository.ProductoRepository;
import com.novatech.store.repository.PromocionRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final PedidoRepository pedidoRepository;
    private final PagoRepository pagoRepository;
    private final FacturaRepository facturaRepository;
    private final ProductoRepository productoRepository;
    private final ProductoService productoService;
    private final PromocionRepository promocionRepository;
    private final CampanaRepository campanaRepository;
    private final CuotaRepository cuotaRepository;
    private final ConversacionRepository conversacionRepository;
    private final CuotaService cuotaService;

    public DashboardService(PedidoRepository pedidoRepository,
                            PagoRepository pagoRepository,
                            FacturaRepository facturaRepository,
                            ProductoRepository productoRepository,
                            ProductoService productoService,
                            PromocionRepository promocionRepository,
                            CampanaRepository campanaRepository,
                            CuotaRepository cuotaRepository,
                            ConversacionRepository conversacionRepository,
                            CuotaService cuotaService) {
        this.pedidoRepository = pedidoRepository;
        this.pagoRepository = pagoRepository;
        this.facturaRepository = facturaRepository;
        this.productoRepository = productoRepository;
        this.productoService = productoService;
        this.promocionRepository = promocionRepository;
        this.campanaRepository = campanaRepository;
        this.cuotaRepository = cuotaRepository;
        this.conversacionRepository = conversacionRepository;
        this.cuotaService = cuotaService;
    }

    public DashboardKpiResponse kpis() {
        cuotaService.actualizarVencidas();

        LocalDateTime inicioMes = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime inicioHoy = LocalDate.now().atStartOfDay();
        List<Pedido> pedidos = pedidoRepository.findAll();
        List<Pago> pagos = pagoRepository.findAll();
        List<Factura> facturas = facturaRepository.findAll();
        List<Producto> productos = productoRepository.findAll();

        DashboardKpiResponse res = new DashboardKpiResponse();

        // --- Ventas ---
        BigDecimal ventasTotales = BigDecimal.ZERO;
        BigDecimal ventasMes = BigDecimal.ZERO;
        BigDecimal ventasHoy = BigDecimal.ZERO;
        long pedidosMes = 0;
        long pedidosHoy = 0;
        long pedidosPendientes = 0;
        long pedidosPagados = 0;
        Set<Integer> clientesActivos = new HashSet<>();
        Map<String, Long> conteoEstados = new HashMap<>();

        for (Pedido p : pedidos) {
            BigDecimal total = p.getTotal() != null ? p.getTotal() : BigDecimal.ZERO;
            ventasTotales = ventasTotales.add(total);

            if (p.getFecha() != null && !p.getFecha().isBefore(inicioMes)) {
                ventasMes = ventasMes.add(total);
                pedidosMes++;
            }

            if (p.getFecha() != null && !p.getFecha().isBefore(inicioHoy)) {
                ventasHoy = ventasHoy.add(total);
                pedidosHoy++;
            }

            String estado = p.getEstado() != null ? p.getEstado() : "SIN_ESTADO";
            conteoEstados.merge(estado, 1L, Long::sum);

            if ("PENDIENTE".equals(estado) || "PARCIAL".equals(estado)) {
                pedidosPendientes++;
            }
            if ("PAGADO".equals(estado)) {
                pedidosPagados++;
            }

            if (p.getUsuario() != null && p.getUsuario().getIdUsuario() != null) {
                clientesActivos.add(p.getUsuario().getIdUsuario());
            }
        }

        res.setVentasTotales(ventasTotales);
        res.setVentasMes(ventasMes);
        res.setVentasHoy(ventasHoy);
        res.setPedidosTotal(pedidos.size());
        res.setPedidosMes(pedidosMes);
        res.setPedidosHoy(pedidosHoy);
        res.setPedidosPendientes(pedidosPendientes);
        res.setPedidosPagados(pedidosPagados);
        res.setTicketPromedio(pedidos.isEmpty()
                ? BigDecimal.ZERO
                : ventasTotales.divide(BigDecimal.valueOf(pedidos.size()), 2, RoundingMode.HALF_UP));
        res.setClientesActivos(clientesActivos.size());

        long totalPedidos = pedidos.size();
        List<EstadoCantidadDto> porEstado = conteoEstados.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .map(e -> new EstadoCantidadDto(
                        e.getKey(),
                        e.getValue(),
                        totalPedidos == 0 ? 0 : (e.getValue() * 100.0 / totalPedidos)))
                .toList();
        res.setPedidosPorEstado(porEstado);

        // --- Cobranza ---
        BigDecimal cobradoTotal = BigDecimal.ZERO;
        BigDecimal cobradoMes = BigDecimal.ZERO;
        Map<Integer, BigDecimal> pagosPorPedido = new HashMap<>();
        for (Pago pago : pagos) {
            if (!pagoAprobado(pago)) {
                continue;
            }
            BigDecimal monto = pago.getMonto() != null ? pago.getMonto() : BigDecimal.ZERO;
            cobradoTotal = cobradoTotal.add(monto);
            if (pago.getFechaPago() != null && !pago.getFechaPago().isBefore(inicioMes)) {
                cobradoMes = cobradoMes.add(monto);
            }
            if (pago.getPedido() != null && pago.getPedido().getIdPedido() != null) {
                int id = pago.getPedido().getIdPedido();
                pagosPorPedido.merge(id, monto, BigDecimal::add);
            }
        }
        res.setCobradoTotal(cobradoTotal);
        res.setCobradoMes(cobradoMes);
        res.setPagosRegistrados(pagos.size());
        res.setPagosPendientesAprobar(pagos.stream()
                .filter(p -> "PENDIENTE".equalsIgnoreCase(p.getEstado()))
                .count());

        BigDecimal cartera = BigDecimal.ZERO;
        for (Pedido p : pedidos) {
            BigDecimal total = p.getTotal() != null ? p.getTotal() : BigDecimal.ZERO;
            BigDecimal pagado = pagosPorPedido.getOrDefault(p.getIdPedido(), BigDecimal.ZERO);
            cartera = cartera.add(total.subtract(pagado).max(BigDecimal.ZERO));
        }
        res.setCarteraPendiente(cartera);

        // --- Facturación ---
        BigDecimal facturadoTotal = BigDecimal.ZERO;
        BigDecimal facturadoMes = BigDecimal.ZERO;
        BigDecimal ivaMes = BigDecimal.ZERO;
        long facturasEmitidas = 0;
        long facturasAnuladas = 0;

        for (Factura f : facturas) {
            if ("EMITIDA".equals(f.getEstado())) {
                facturasEmitidas++;
                BigDecimal total = f.getTotal() != null ? f.getTotal() : BigDecimal.ZERO;
                facturadoTotal = facturadoTotal.add(total);
                if (f.getFechaEmision() != null && !f.getFechaEmision().isBefore(inicioMes)) {
                    facturadoMes = facturadoMes.add(total);
                    ivaMes = ivaMes.add(f.getIva() != null ? f.getIva() : BigDecimal.ZERO);
                }
            } else if ("ANULADA".equals(f.getEstado())) {
                facturasAnuladas++;
            }
        }
        res.setFacturadoTotal(facturadoTotal);
        res.setFacturadoMes(facturadoMes);
        res.setIvaMes(ivaMes);
        res.setFacturasEmitidas(facturasEmitidas);
        res.setFacturasAnuladas(facturasAnuladas);

        // --- Catálogo ---
        res.setProductosTotal(productos.size());
        res.setProductosBajoStock(productoService.contarStockBajo());

        // --- CRM / crédito ---
        res.setPromocionesActivas(promocionRepository.findAll().stream()
                .filter(p -> "ACTIVA".equals(p.getEstado()))
                .count());
        res.setCampanasPendientes(campanaRepository.findAll().stream()
                .filter(c -> "BORRADOR".equals(c.getEstado()) || "PROGRAMADA".equals(c.getEstado()))
                .count());
        res.setCrmPendientes(conversacionRepository.findAll().stream()
                .filter(c -> {
                    String est = c.getEstado() != null ? c.getEstado().toUpperCase() : "";
                    return "ABIERTA".equals(est) || "PENDIENTE".equals(est) || "NUEVA".equals(est);
                })
                .count());
        res.setCuotasVencidas(cuotaRepository.findByEstado("VENCIDA").size());
        LocalDate limite = LocalDate.now().plusDays(7);
        res.setCuotasPorVencer(cuotaRepository.findAll().stream()
                .filter(c -> "PENDIENTE".equals(c.getEstado()))
                .filter(c -> !c.getFechaVencimiento().isBefore(LocalDate.now()))
                .filter(c -> !c.getFechaVencimiento().isAfter(limite))
                .count());

        // --- Recientes ---
        res.setUltimosPedidos(pedidos.stream()
                .sorted(Comparator.comparing(Pedido::getFecha, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(5)
                .map(this::toPedidoReciente)
                .toList());

        res.setUltimasFacturas(facturas.stream()
                .filter(f -> f.getFechaEmision() != null)
                .sorted(Comparator.comparing(Factura::getFechaEmision).reversed())
                .limit(5)
                .map(this::toFacturaReciente)
                .toList());

        return res;
    }

    private boolean pagoAprobado(Pago pago) {
        String est = pago.getEstado();
        return est == null || est.isBlank() || "APROBADO".equalsIgnoreCase(est);
    }

    private PedidoRecienteDto toPedidoReciente(Pedido p) {
        PedidoRecienteDto dto = new PedidoRecienteDto();
        dto.setIdPedido(p.getIdPedido());
        dto.setClienteNombre(p.getUsuario() != null ? p.getUsuario().getNombre() : "—");
        dto.setTotal(p.getTotal());
        dto.setEstado(p.getEstado());
        dto.setFecha(p.getFecha());
        return dto;
    }

    private FacturaRecienteDto toFacturaReciente(Factura f) {
        FacturaRecienteDto dto = new FacturaRecienteDto();
        dto.setIdFactura(f.getIdFactura());
        dto.setNumeroFactura(f.getNumeroFactura());
        if (f.getPedido() != null) {
            dto.setIdPedido(f.getPedido().getIdPedido());
            if (f.getPedido().getUsuario() != null) {
                dto.setClienteNombre(f.getPedido().getUsuario().getNombre());
            }
        }
        dto.setTotal(f.getTotal());
        dto.setEstado(f.getEstado());
        dto.setFechaEmision(f.getFechaEmision());
        return dto;
    }
}
