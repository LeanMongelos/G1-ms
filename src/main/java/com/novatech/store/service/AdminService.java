package com.novatech.store.service;

import com.novatech.store.dto.AdminBuscarItemDto;
import com.novatech.store.dto.AdminBuscarResponse;
import com.novatech.store.dto.AdminNotificacionDto;
import com.novatech.store.entity.Conversacion;
import com.novatech.store.entity.Cuota;
import com.novatech.store.entity.Envio;
import com.novatech.store.entity.Factura;
import com.novatech.store.entity.Pago;
import com.novatech.store.entity.Pedido;
import com.novatech.store.entity.PerfilCliente;
import com.novatech.store.entity.Presupuesto;
import com.novatech.store.entity.Remito;
import com.novatech.store.repository.ConversacionRepository;
import com.novatech.store.repository.CuotaRepository;
import com.novatech.store.repository.EnvioRepository;
import com.novatech.store.repository.FacturaRepository;
import com.novatech.store.repository.PagoRepository;
import com.novatech.store.repository.PedidoRepository;
import com.novatech.store.repository.PerfilClienteRepository;
import com.novatech.store.repository.PresupuestoRepository;
import com.novatech.store.repository.RemitoRepository;
import com.novatech.store.util.PagoUtil;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private static final int LIMITE_GRUPO = 5;
    private static final int LIMITE_NOTIFICACIONES = 25;

    private final PerfilClienteRepository perfilRepository;
    private final FacturaRepository facturaRepository;
    private final RemitoRepository remitoRepository;
    private final PresupuestoRepository presupuestoRepository;
    private final EnvioRepository envioRepository;
    private final PedidoRepository pedidoRepository;
    private final PagoRepository pagoRepository;
    private final CuotaRepository cuotaRepository;
    private final ConversacionRepository conversacionRepository;
    private final CuotaService cuotaService;

    public AdminService(PerfilClienteRepository perfilRepository,
                        FacturaRepository facturaRepository,
                        RemitoRepository remitoRepository,
                        PresupuestoRepository presupuestoRepository,
                        EnvioRepository envioRepository,
                        PedidoRepository pedidoRepository,
                        PagoRepository pagoRepository,
                        CuotaRepository cuotaRepository,
                        ConversacionRepository conversacionRepository,
                        CuotaService cuotaService) {
        this.perfilRepository = perfilRepository;
        this.facturaRepository = facturaRepository;
        this.remitoRepository = remitoRepository;
        this.presupuestoRepository = presupuestoRepository;
        this.envioRepository = envioRepository;
        this.pedidoRepository = pedidoRepository;
        this.pagoRepository = pagoRepository;
        this.cuotaRepository = cuotaRepository;
        this.conversacionRepository = conversacionRepository;
        this.cuotaService = cuotaService;
    }

    public AdminBuscarResponse buscar(String q) {
        AdminBuscarResponse res = new AdminBuscarResponse();
        if (q == null || q.isBlank() || q.trim().length() < 2) {
            return res;
        }
        String termino = q.trim();
        String terminoLower = termino.toLowerCase();

        res.setClientes(perfilRepository.buscarActivos(termino, null).stream()
                .limit(LIMITE_GRUPO)
                .map(this::toClienteItem)
                .toList());

        res.setFacturas(facturaRepository.findAll().stream()
                .filter(f -> coincideFactura(f, termino, terminoLower))
                .sorted(Comparator.comparing(Factura::getFechaEmision,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(LIMITE_GRUPO)
                .map(this::toFacturaItem)
                .toList());

        res.setRemitos(remitoRepository.findAll().stream()
                .filter(r -> coincideRemito(r, termino, terminoLower))
                .sorted(Comparator.comparing(Remito::getFecha,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(LIMITE_GRUPO)
                .map(this::toRemitoItem)
                .toList());

        res.setPresupuestos(presupuestoRepository.findAll().stream()
                .filter(p -> coincidePresupuesto(p, termino, terminoLower))
                .sorted(Comparator.comparing(Presupuesto::getFecha,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(LIMITE_GRUPO)
                .map(this::toPresupuestoItem)
                .toList());

        return res;
    }

    public List<AdminNotificacionDto> notificaciones() {
        cuotaService.actualizarVencidas();

        List<AdminNotificacionDto> lista = new ArrayList<>();
        LocalDateTime hace24h = LocalDateTime.now().minusHours(24);

        for (Pedido p : pedidoRepository.findAll()) {
            if (p.getFecha() != null && !p.getFecha().isBefore(hace24h)) {
                String canal = p.getCanalOrigen() != null ? p.getCanalOrigen().toUpperCase() : "";
                if ("WEB".equals(canal) || "POS".equals(canal)) {
                    lista.add(notif(
                            "VENTA_ECOMMERCE",
                            "Nueva venta ecommerce",
                            "Pedido #" + p.getIdPedido()
                                    + " — " + nombreCliente(p)
                                    + " ($" + formatoMonto(p.getTotal()) + ")",
                            "/admin/pedidos",
                            p.getFecha()));
                }
            }

            String estado = p.getEstado() != null ? p.getEstado().toUpperCase() : "";
            if ("PENDIENTE".equals(estado) || "PARCIAL".equals(estado)) {
                BigDecimal saldo = calcularSaldo(p);
                if (saldo.compareTo(BigDecimal.ZERO) > 0) {
                    lista.add(notif(
                            "COBRO_PENDIENTE",
                            "Cobro pendiente",
                            "Pedido #" + p.getIdPedido()
                                    + " — saldo $" + formatoMonto(saldo),
                            "/admin/pagos",
                            p.getFecha()));
                }
            }
        }

        for (Pago pago : pagoRepository.findAll()) {
            if ("PENDIENTE".equalsIgnoreCase(pago.getEstado())) {
                Integer idPedido = pago.getPedido() != null ? pago.getPedido().getIdPedido() : null;
                lista.add(notif(
                        "PAGO_PENDIENTE",
                        "Pago pendiente de aprobar",
                        "Pago #" + pago.getIdPago()
                                + (idPedido != null ? " — pedido #" + idPedido : "")
                                + " ($" + formatoMonto(pago.getMonto()) + ")",
                        "/admin/pagos",
                        pago.getFechaPago() != null ? pago.getFechaPago() : LocalDateTime.now()));
            }
        }

        for (Cuota cuota : cuotaRepository.findByEstado("VENCIDA")) {
            lista.add(notif(
                    "CUOTA_VENCIDA",
                    "Cuota vencida",
                    "Cuota #" + cuota.getNumeroCuota()
                            + " — $" + formatoMonto(cuota.getMonto()),
                    "/admin/creditos",
                    cuota.getFechaVencimiento() != null
                            ? cuota.getFechaVencimiento().atStartOfDay()
                            : LocalDateTime.now()));
        }

        for (Conversacion conv : conversacionRepository.findAll()) {
            if (!CrmMetricsService.conversacionPendiente(conv)) {
                continue;
            }
            lista.add(notif(
                    "CRM_MENSAJE",
                    "Mensaje CRM sin atender",
                    (conv.getContactoNombre() != null ? conv.getContactoNombre() : "Contacto")
                            + " — " + truncar(conv.getVistaPrevia(), 60),
                    "/admin/crm/inbox",
                    conv.getUltimaActividad() != null ? conv.getUltimaActividad() : conv.getFechaCreacion()));
        }

        return lista.stream()
                .sorted(Comparator.comparing(AdminNotificacionDto::getFecha,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(LIMITE_NOTIFICACIONES)
                .toList();
    }

    private AdminBuscarItemDto toClienteItem(PerfilCliente p) {
        AdminBuscarItemDto item = new AdminBuscarItemDto();
        item.setTipo("CLIENTE");
        item.setId(p.getIdCliente());
        item.setTitulo(p.getUsuario() != null ? p.getUsuario().getNombre() : "Cliente");
        item.setSubtitulo(p.getUsuario() != null ? p.getUsuario().getEmail() : null);
        item.setLink("/admin/crm/clientes/" + p.getIdCliente());
        return item;
    }

    private AdminBuscarItemDto toFacturaItem(Factura f) {
        AdminBuscarItemDto item = new AdminBuscarItemDto();
        item.setTipo("FACTURA");
        item.setId(f.getIdFactura());
        item.setTitulo(f.getNumeroFactura() != null ? f.getNumeroFactura() : "Factura #" + f.getIdFactura());
        if (f.getPedido() != null && f.getPedido().getUsuario() != null) {
            item.setSubtitulo(f.getPedido().getUsuario().getNombre());
        } else if (f.getPresupuesto() != null && f.getPresupuesto().getCliente() != null
                && f.getPresupuesto().getCliente().getUsuario() != null) {
            item.setSubtitulo(f.getPresupuesto().getCliente().getUsuario().getNombre());
        } else {
            item.setSubtitulo(f.getEstado());
        }
        item.setLink("/admin/facturacion");
        return item;
    }

    private AdminBuscarItemDto toRemitoItem(Remito r) {
        AdminBuscarItemDto item = new AdminBuscarItemDto();
        item.setTipo("REMITO");
        item.setId(r.getIdRemito());
        item.setTitulo(r.getNumeroRemito() != null ? r.getNumeroRemito() : "Remito #" + r.getIdRemito());
        item.setSubtitulo(r.getCliente() != null && r.getCliente().getUsuario() != null
                ? r.getCliente().getUsuario().getNombre()
                : r.getEstado());
        item.setLink("/admin/remitos/" + r.getIdRemito());
        return item;
    }

    private AdminBuscarItemDto toPresupuestoItem(Presupuesto p) {
        AdminBuscarItemDto item = new AdminBuscarItemDto();
        item.setTipo("PRESUPUESTO");
        item.setId(p.getIdPresupuesto());
        item.setTitulo(p.getNumeroPresupuesto() != null ? p.getNumeroPresupuesto() : "Presupuesto #" + p.getIdPresupuesto());
        item.setSubtitulo((p.getCliente() != null && p.getCliente().getUsuario() != null
                ? p.getCliente().getUsuario().getNombre()
                : "Cliente") + " — $" + formatoMonto(p.getTotal()) + " · " + p.getEstado());
        item.setLink("/admin/presupuestos/" + p.getIdPresupuesto());
        return item;
    }

    private boolean coincidePresupuesto(Presupuesto p, String termino, String q) {
        if (contiene(p.getNumeroPresupuesto(), q)) return true;
        if (contiene(p.getEstado(), q)) return true;
        if (contiene(p.getNotas(), q)) return true;
        if (p.getIdPresupuesto() != null && p.getIdPresupuesto().toString().equals(termino)) return true;
        if (p.getCliente() != null && p.getCliente().getUsuario() != null) {
            if (contiene(p.getCliente().getUsuario().getNombre(), q)) return true;
            if (contiene(p.getCliente().getUsuario().getEmail(), q)) return true;
        }
        return false;
    }

    private boolean coincideRemito(Remito r, String termino, String q) {
        if (contiene(r.getNumeroRemito(), q)) return true;
        if (contiene(r.getDireccionEntrega(), q)) return true;
        if (contiene(r.getEstado(), q)) return true;
        if (r.getIdRemito() != null && r.getIdRemito().toString().equals(termino)) return true;
        if (r.getPedido() != null && r.getPedido().getIdPedido() != null
                && r.getPedido().getIdPedido().toString().equals(termino)) return true;
        if (r.getPresupuesto() != null && r.getPresupuesto().getIdPresupuesto() != null
                && r.getPresupuesto().getIdPresupuesto().toString().equals(termino)) return true;
        if (r.getCliente() != null && r.getCliente().getUsuario() != null) {
            if (contiene(r.getCliente().getUsuario().getNombre(), q)) return true;
        }
        return false;
    }

    private boolean coincideFactura(Factura f, String termino, String q) {
        if (contiene(f.getNumeroFactura(), q)) return true;
        if (contiene(f.getCuitCliente(), q)) return true;
        if (contiene(f.getEstado(), q)) return true;
        if (f.getIdFactura() != null && f.getIdFactura().toString().equals(termino)) return true;
        if (f.getPedido() != null) {
            if (f.getPedido().getIdPedido() != null
                    && f.getPedido().getIdPedido().toString().equals(termino)) return true;
            if (f.getPedido().getUsuario() != null) {
                if (contiene(f.getPedido().getUsuario().getNombre(), q)) return true;
                if (contiene(f.getPedido().getUsuario().getEmail(), q)) return true;
            }
        }
        return false;
    }

    private boolean coincideEnvio(Envio e, String termino, String q) {
        if (contiene(e.getNumeroTracking(), q)) return true;
        if (contiene(e.getDireccionEnvio(), q)) return true;
        if (contiene(e.getEmpresaLogistica(), q)) return true;
        if (contiene(e.getEstadoEnvio(), q)) return true;
        if (e.getIdEnvio() != null && e.getIdEnvio().toString().equals(termino)) return true;
        if (e.getPedido() != null && e.getPedido().getIdPedido() != null
                && e.getPedido().getIdPedido().toString().equals(termino)) return true;
        return false;
    }

    private boolean coincidePedido(Pedido p, String termino, String q) {
        if (p.getIdPedido() != null && p.getIdPedido().toString().equals(termino)) return true;
        if (contiene(p.getNotas(), q)) return true;
        if (contiene(p.getEstado(), q)) return true;
        if (p.getUsuario() != null) {
            if (contiene(p.getUsuario().getNombre(), q)) return true;
            if (contiene(p.getUsuario().getEmail(), q)) return true;
        }
        return false;
    }

    private AdminNotificacionDto notif(String tipo, String titulo, String mensaje,
                                       String link, LocalDateTime fecha) {
        AdminNotificacionDto dto = new AdminNotificacionDto();
        dto.setTipo(tipo);
        dto.setTitulo(titulo);
        dto.setMensaje(mensaje);
        dto.setLink(link);
        dto.setFecha(fecha);
        dto.setLeida(false);
        return dto;
    }

    private BigDecimal calcularSaldo(Pedido pedido) {
        BigDecimal total = pedido.getTotal() != null ? pedido.getTotal() : BigDecimal.ZERO;
        if (pedido.getIdPedido() == null) {
            return total.max(BigDecimal.ZERO);
        }
        return PagoUtil.saldoPedido(total, pagoRepository.findByPedidoIdPedido(pedido.getIdPedido()));
    }

    private String nombreCliente(Pedido p) {
        return p.getUsuario() != null && p.getUsuario().getNombre() != null
                ? p.getUsuario().getNombre()
                : "Cliente";
    }

    private String formatoMonto(BigDecimal monto) {
        return monto != null ? monto.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString() : "0.00";
    }

    private boolean contiene(String texto, String q) {
        return texto != null && texto.toLowerCase().contains(q);
    }

    private String truncar(String texto, int max) {
        if (texto == null) return "";
        return texto.length() <= max ? texto : texto.substring(0, max) + "…";
    }
}
