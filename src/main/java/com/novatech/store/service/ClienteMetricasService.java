package com.novatech.store.service;

import com.novatech.store.entity.DetallePedido;
import com.novatech.store.entity.Factura;
import com.novatech.store.entity.Pedido;
import com.novatech.store.entity.PerfilCliente;
import com.novatech.store.exception.ResourceNotFoundException;
import com.novatech.store.repository.DetallePedidoRepository;
import com.novatech.store.repository.FacturaRepository;
import com.novatech.store.repository.PedidoRepository;
import com.novatech.store.repository.PerfilClienteRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ClienteMetricasService {

    private final PerfilClienteRepository perfilRepository;
    private final PedidoRepository pedidoRepository;
    private final FacturaRepository facturaRepository;
    private final DetallePedidoRepository detallePedidoRepository;

    public ClienteMetricasService(
            PerfilClienteRepository perfilRepository,
            PedidoRepository pedidoRepository,
            FacturaRepository facturaRepository,
            DetallePedidoRepository detallePedidoRepository) {
        this.perfilRepository = perfilRepository;
        this.pedidoRepository = pedidoRepository;
        this.facturaRepository = facturaRepository;
        this.detallePedidoRepository = detallePedidoRepository;
    }

    public Map<String, Object> metricas(Integer idCliente) {
        PerfilCliente perfil = perfilRepository.findById(idCliente)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado: " + idCliente));
        List<Factura> facturas = facturasDeCliente(perfil);
        return calcularMetricas(facturas, perfil.getLimiteCredito());
    }

    public Map<String, Object> historial(Integer idCliente) {
        PerfilCliente perfil = perfilRepository.findById(idCliente)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado: " + idCliente));
        Integer idUsuario = perfil.getUsuario() != null ? perfil.getUsuario().getIdUsuario() : null;
        List<Map<String, Object>> pedidos = new ArrayList<>();
        List<Map<String, Object>> productos = new ArrayList<>();
        if (idUsuario != null) {
            List<Pedido> lista = pedidoRepository.findByUsuario_IdUsuarioOrderByFechaDesc(idUsuario);
            for (Pedido p : lista.stream().limit(10).toList()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("idPedido", p.getIdPedido());
                row.put("fecha", p.getFecha());
                row.put("estado", p.getEstado());
                row.put("total", p.getTotal());
                pedidos.add(row);
            }
            Map<String, Map<String, Object>> acc = new LinkedHashMap<>();
            for (Pedido p : lista) {
                for (DetallePedido d : detallePedidoRepository.findByPedidoIdPedido(p.getIdPedido())) {
                    String desc = d.getProducto() != null ? d.getProducto().getNombre() : "Producto";
                    String key = desc.toLowerCase();
                    Map<String, Object> prev = acc.computeIfAbsent(key, k -> {
                        Map<String, Object> m = new LinkedHashMap<>();
                        m.put("descripcion", desc);
                        m.put("cantidad", 0);
                        m.put("monto", BigDecimal.ZERO);
                        return m;
                    });
                    prev.put("cantidad", (Integer) prev.get("cantidad") + (d.getCantidad() != null ? d.getCantidad() : 0));
                    BigDecimal sub = d.getPrecioUnitario() != null && d.getCantidad() != null
                            ? d.getPrecioUnitario().multiply(BigDecimal.valueOf(d.getCantidad()))
                            : BigDecimal.ZERO;
                    prev.put("monto", ((BigDecimal) prev.get("monto")).add(sub));
                }
            }
            productos = acc.values().stream()
                    .sorted((a, b) -> ((BigDecimal) b.get("monto")).compareTo((BigDecimal) a.get("monto")))
                    .limit(10)
                    .collect(Collectors.toList());
        }
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("pedidos", pedidos);
        out.put("productosFacturados", productos);
        return out;
    }

    private List<Factura> facturasDeCliente(PerfilCliente perfil) {
        if (perfil.getUsuario() == null || perfil.getUsuario().getIdUsuario() == null) {
            return List.of();
        }
        List<Pedido> pedidos = pedidoRepository.findByUsuario_IdUsuarioOrderByFechaDesc(
                perfil.getUsuario().getIdUsuario());
        List<Factura> facturas = new ArrayList<>();
        for (Pedido p : pedidos) {
            facturas.addAll(facturaRepository.findByPedidoIdPedido(p.getIdPedido()));
        }
        return facturas;
    }

    private Map<String, Object> calcularMetricas(List<Factura> facturas, BigDecimal limiteCredito) {
        LocalDateTime ahora = LocalDateTime.now();
        List<Factura> ventas = facturas.stream()
                .filter(f -> esVenta(f.getEstado()))
                .sorted(Comparator.comparing(Factura::getFechaEmision, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();

        BigDecimal totalComprado = ventas.stream()
                .map(f -> f.getTotal() != null ? f.getTotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        int cantidadCompras = ventas.size();
        BigDecimal ticketPromedio = cantidadCompras > 0
                ? totalComprado.divide(BigDecimal.valueOf(cantidadCompras), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        LocalDateTime primera = ventas.isEmpty() ? null : ventas.get(0).getFechaEmision();
        LocalDateTime ultima = ventas.isEmpty() ? null : ventas.get(ventas.size() - 1).getFechaEmision();
        Long diasDesdeUltima = ultima != null ? ChronoUnit.DAYS.between(ultima.toLocalDate(), ahora.toLocalDate()) : null;

        Long frecuenciaMediaDias = null;
        boolean enRiesgo = false;
        if (primera != null && ultima != null && cantidadCompras > 1) {
            long span = ChronoUnit.DAYS.between(primera.toLocalDate(), ultima.toLocalDate());
            frecuenciaMediaDias = Math.max(1, Math.round((double) span / (cantidadCompras - 1)));
            if (diasDesdeUltima != null && diasDesdeUltima > frecuenciaMediaDias * 1.5) {
                enRiesgo = true;
            }
        }

        BigDecimal saldoActual = BigDecimal.ZERO;
        BigDecimal deudaVencida = BigDecimal.ZERO;
        Map<String, BigDecimal> aging = new LinkedHashMap<>();
        aging.put("bucket0_30", BigDecimal.ZERO);
        aging.put("bucket31_60", BigDecimal.ZERO);
        aging.put("bucket61_90", BigDecimal.ZERO);
        aging.put("bucket90", BigDecimal.ZERO);

        for (Factura f : facturas) {
            if (!esDeuda(f.getEstado())) continue;
            BigDecimal t = f.getTotal() != null ? f.getTotal() : BigDecimal.ZERO;
            saldoActual = saldoActual.add(t);
            LocalDateTime ref = f.getFechaEmision() != null ? f.getFechaEmision() : ahora;
            long dias = ChronoUnit.DAYS.between(ref.toLocalDate(), ahora.toLocalDate());
            if ("VENCIDA".equalsIgnoreCase(f.getEstado()) || dias > 0) {
                deudaVencida = deudaVencida.add(t);
            }
            if (dias <= 30) aging.put("bucket0_30", aging.get("bucket0_30").add(t));
            else if (dias <= 60) aging.put("bucket31_60", aging.get("bucket31_60").add(t));
            else if (dias <= 90) aging.put("bucket61_90", aging.get("bucket61_90").add(t));
            else aging.put("bucket90", aging.get("bucket90").add(t));
        }

        int scorePago = 100;
        if (!ventas.isEmpty()) {
            long vencidas = ventas.stream().filter(f -> "VENCIDA".equalsIgnoreCase(f.getEstado())).count();
            scorePago = (int) Math.round(((double) (ventas.size() - vencidas) / ventas.size()) * 100);
        }
        String semaforo = deudaVencida.compareTo(BigDecimal.ZERO) > 0 || scorePago < 60 ? "ROJO"
                : scorePago < 85 ? "AMARILLO" : "VERDE";

        String segmento = calcularSegmento(cantidadCompras, diasDesdeUltima, deudaVencida, enRiesgo);

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("totalComprado", totalComprado);
        out.put("cantidadCompras", cantidadCompras);
        out.put("ticketPromedio", ticketPromedio);
        out.put("primeraCompra", primera);
        out.put("ultimaCompra", ultima);
        out.put("diasDesdeUltimaCompra", diasDesdeUltima);
        out.put("frecuenciaMediaDias", frecuenciaMediaDias);
        out.put("enRiesgo", enRiesgo);
        out.put("saldoActual", saldoActual);
        out.put("deudaVencida", deudaVencida);
        out.put("aging", aging);
        out.put("scorePago", scorePago);
        out.put("semaforoPago", semaforo);
        out.put("limiteCredito", limiteCredito);
        out.put("segmento", segmento);
        return out;
    }

    private boolean esVenta(String estado) {
        if (estado == null) return false;
        return switch (estado.toUpperCase()) {
            case "PAGADA", "EMITIDA", "PENDIENTE", "VENCIDA", "PENDIENTE_CAE" -> true;
            default -> false;
        };
    }

    private boolean esDeuda(String estado) {
        if (estado == null) return false;
        return switch (estado.toUpperCase()) {
            case "PENDIENTE", "VENCIDA", "EMITIDA", "PENDIENTE_CAE" -> true;
            default -> false;
        };
    }

    private String calcularSegmento(int compras, Long diasDesdeUltima, BigDecimal deudaVencida, boolean enRiesgo) {
        if (compras == 0) return "INACTIVO";
        if (deudaVencida.compareTo(BigDecimal.ZERO) > 0) return "MOROSO";
        if (enRiesgo || (diasDesdeUltima != null && diasDesdeUltima > 180)) return "EN_RIESGO";
        if (compras >= 6) return "VIP";
        if (compras >= 3) return "RECURRENTE";
        return "NUEVO";
    }
}
