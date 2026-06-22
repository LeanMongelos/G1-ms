package com.novatech.store.service;

import com.novatech.store.config.PlantillaTemplates;
import com.novatech.store.dto.PlantillaRenderResponse;
import com.novatech.store.entity.*;
import com.novatech.store.repository.*;
import com.novatech.store.util.NumeroALetrasUtil;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PlantillaRenderService {

    private static final DateTimeFormatter FMT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FMT_DATETIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final PlantillaImpresionRepository plantillaRepository;
    private final EmisorRepository emisorRepository;
    private final FacturaRepository facturaRepository;
    private final PresupuestoRepository presupuestoRepository;
    private final RemitoRepository remitoRepository;
    private final CuotaRepository cuotaRepository;
    private final PlanCuotasRepository planRepository;

    public PlantillaRenderService(
            PlantillaImpresionRepository plantillaRepository,
            EmisorRepository emisorRepository,
            FacturaRepository facturaRepository,
            PresupuestoRepository presupuestoRepository,
            RemitoRepository remitoRepository,
            CuotaRepository cuotaRepository,
            PlanCuotasRepository planRepository) {
        this.plantillaRepository = plantillaRepository;
        this.emisorRepository = emisorRepository;
        this.facturaRepository = facturaRepository;
        this.presupuestoRepository = presupuestoRepository;
        this.remitoRepository = remitoRepository;
        this.cuotaRepository = cuotaRepository;
        this.planRepository = planRepository;
    }

    public PlantillaRenderResponse preview(Integer idPlantilla) {
        PlantillaImpresion p = plantillaRepository.findById(idPlantilla)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plantilla no encontrada"));
        Map<String, String> vars = demoVars(p.getTipo());
        return render(p, vars);
    }

    public PlantillaRenderResponse previewDefault(String tipo) {
        PlantillaImpresion p = obtenerDefault(tipo);
        return render(p, demoVars(tipo));
    }

    public PlantillaRenderResponse renderFactura(Integer idFactura, Integer plantillaId) {
        Factura f = facturaRepository.findById(idFactura)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Factura no encontrada"));
        PlantillaImpresion p = resolverPlantilla("FACTURA", plantillaId);
        return render(p, varsFactura(f));
    }

    public PlantillaRenderResponse renderPresupuesto(Integer idPresupuesto, Integer plantillaId) {
        Presupuesto p = presupuestoRepository.findById(idPresupuesto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Presupuesto no encontrado"));
        PlantillaImpresion tpl = resolverPlantilla("PRESUPUESTO", plantillaId);
        return render(tpl, varsPresupuesto(p));
    }

    public PlantillaRenderResponse renderRemito(Integer idRemito, Integer plantillaId) {
        Remito r = remitoRepository.findById(idRemito)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Remito no encontrado"));
        PlantillaImpresion tpl = resolverPlantilla("REMITO", plantillaId);
        return render(tpl, varsRemito(r));
    }

    public PlantillaImpresion obtenerDefault(String tipo) {
        return plantillaRepository.findFirstByTipoAndEsDefaultTrueAndActivoTrueOrderByIdPlantillaAsc(tipo)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No hay plantilla default para " + tipo));
    }

    private PlantillaImpresion resolverPlantilla(String tipo, Integer plantillaId) {
        if (plantillaId != null) {
            PlantillaImpresion p = plantillaRepository.findById(plantillaId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plantilla no encontrada"));
            if (!tipo.equalsIgnoreCase(p.getTipo())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La plantilla no corresponde al tipo");
            }
            return p;
        }
        return obtenerDefault(tipo);
    }

    public PlantillaRenderResponse render(PlantillaImpresion plantilla, Map<String, String> vars) {
        String[] parts = extractParts(plantilla.getContenidoJson(), plantilla.getTipo());
        String html = parts[0];
        String css = parts[1];
        for (Map.Entry<String, String> e : vars.entrySet()) {
            html = html.replace("{{" + e.getKey() + "}}", safe(e.getValue()));
        }
        html = html.replaceAll("\\{\\{[^}]+\\}\\}", "");
        return new PlantillaRenderResponse(html, css, plantilla.getIdPlantilla(), plantilla.getTipo());
    }

    private String[] extractParts(String contenidoJson, String tipo) {
        if (contenidoJson != null && contenidoJson.contains("\"html\"")) {
            String html = jsonField(contenidoJson, "html");
            String css = jsonField(contenidoJson, "css");
            if (html != null) {
                return new String[] { html, css != null ? css : PlantillaTemplates.CSS };
            }
        }
        return partsFromDefault(tipo);
    }

    private String[] partsFromDefault(String tipo) {
        String json = switch (tipo != null ? tipo.toUpperCase(Locale.ROOT) : "") {
            case "PRESUPUESTO" -> PlantillaTemplates.jsonPresupuesto();
            case "REMITO" -> PlantillaTemplates.jsonRemito();
            default -> PlantillaTemplates.jsonFactura();
        };
        String html = jsonField(json, "html");
        String css = jsonField(json, "css");
        if (html == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Plantilla inválida");
        }
        return new String[] { html, css != null ? css : PlantillaTemplates.CSS };
    }

    private static String jsonField(String json, String field) {
        String marker = "\"" + field + "\":\"";
        int start = json.indexOf(marker);
        if (start < 0) {
            return null;
        }
        start += marker.length();
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '\\' && i + 1 < json.length()) {
                char next = json.charAt(i + 1);
                switch (next) {
                    case 'n' -> sb.append('\n');
                    case 'r' -> sb.append('\r');
                    case 't' -> sb.append('\t');
                    case '"' -> sb.append('"');
                    case '\\' -> sb.append('\\');
                    default -> sb.append(next);
                }
                i++;
            } else if (c == '"') {
                break;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private Map<String, String> demoVars(String tipo) {
        Map<String, String> v = baseEmpresaVars();
        v.put("cliente_nombre", "Cliente Demo S.A.");
        v.put("cliente_direccion", "Av. Demo 1234, CABA");
        v.put("cliente_cuit", "30-71234567-9");
        v.put("cliente_condicion_iva", "Responsable Inscripto");
        v.put("vendedor_nombre", "María González");
        v.put("orden_compra", "OC-2026-001");
        v.put("plazos_cobranza", "30 días");
        v.put("fecha_entrega", LocalDate.now().plusDays(7).format(FMT_DATE));
        v.put("direccion_entrega", "Depósito central");
        v.put("observaciones_texto", "Entrega en horario comercial.");
        v.put("forma_pago", "Transferencia bancaria");
        v.put("tasa_financiacion", "0%");
        v.put("plazo_entrega", "48 hs hábiles");
        v.put("garantia", "12 meses");
        v.put("total_subtotal", "$ 100.000,00");
        v.put("total_bonificacion", "$ 5.000,00");
        v.put("total_subtotal_neto", "$ 95.000,00");
        v.put("filas_totales_extra", "<div><span>IVA 21%</span><span>$ 19.950,00</span></div>");
        v.put("total_final", "$ 114.950,00");
        v.put("total_en_letras", NumeroALetrasUtil.pesos(new BigDecimal("114950")));
        v.put("presupuesto_ref", "PR-2026-00042");
        v.put("vigencia", LocalDate.now().plusDays(15).format(FMT_DATE));
        v.put("seccion_cronograma", cronogramaHtml(List.of(
                cuotaDemo(1, "15.000,00"), cuotaDemo(2, "15.000,00"), cuotaDemo(3, "15.000,00"))));

        String t = tipo != null ? tipo.toUpperCase(Locale.ROOT) : "FACTURA";
        if ("FACTURA".equals(t)) {
            v.put("factura_numero", "NV-2026-000001");
            v.put("factura_fecha", LocalDate.now().format(FMT_DATE));
        } else if ("PRESUPUESTO".equals(t)) {
            v.put("presupuesto_numero", "PR-2026-000042");
            v.put("presupuesto_fecha", LocalDate.now().format(FMT_DATE));
        } else {
            v.put("remito_numero", "RM-2026-000015");
            v.put("remito_fecha", LocalDate.now().format(FMT_DATE));
        }

        demoItems(v, 3);
        return v;
    }

    private Map<String, String> varsFactura(Factura f) {
        Map<String, String> v = baseEmpresaVars();
        PerfilCliente cliente = clienteFactura(f);
        if (cliente != null) {
            putCliente(v, cliente, f.getCuitCliente(), f.getCondicionIvaCliente());
        } else if (f.getPedido() != null && f.getPedido().getUsuario() != null) {
            v.put("cliente_nombre", nz(f.getPedido().getUsuario().getNombre()));
            v.put("cliente_direccion", "—");
            v.put("cliente_cuit", nz(f.getCuitCliente()));
            v.put("cliente_condicion_iva", labelIva(f.getCondicionIvaCliente()));
        } else {
            putCliente(v, null, f.getCuitCliente(), f.getCondicionIvaCliente());
        }
        v.put("factura_numero", nz(f.getNumeroFactura()));
        v.put("factura_fecha", f.getFechaEmision() != null ? f.getFechaEmision().format(FMT_DATETIME) : "");
        v.put("vendedor_nombre", "—");
        v.put("orden_compra", "—");
        v.put("plazos_cobranza", labelFormaCobro(f));
        v.put("fecha_entrega", "—");
        v.put("direccion_entrega", direccionCliente(cliente));
        v.put("observaciones_texto", nz(f.getNotas()));
        v.put("forma_pago", labelFormaCobro(f));
        v.put("tasa_financiacion", f.getFormaCobro() != null && f.getFormaCobro().contains("PRESTAMO") ? "Según plan" : "0%");
        v.put("plazo_entrega", "—");
        v.put("garantia", "Según fabricante");
        v.put("presupuesto_ref", f.getPresupuesto() != null ? nz(f.getPresupuesto().getNumeroPresupuesto()) : "—");
        v.put("vigencia", "—");

        BigDecimal sub = nzBd(f.getSubtotal());
        BigDecimal iva = nzBd(f.getIva());
        BigDecimal total = nzBd(f.getTotal());
        v.put("total_subtotal", money(sub));
        v.put("total_bonificacion", "$ 0,00");
        v.put("total_subtotal_neto", money(sub));
        v.put("filas_totales_extra", iva.compareTo(BigDecimal.ZERO) > 0
                ? "<div><span>IVA 21%</span><span>" + money(iva) + "</span></div>" : "");
        v.put("total_final", money(total));
        v.put("total_en_letras", NumeroALetrasUtil.pesos(total));

        putLineas(v, f.getLineas(), false);
        v.put("seccion_cronograma", cronogramaFactura(f));
        return v;
    }

    private Map<String, String> varsPresupuesto(Presupuesto p) {
        Map<String, String> v = baseEmpresaVars();
        putCliente(v, p.getCliente(), null, null);
        v.put("presupuesto_numero", nz(p.getNumeroPresupuesto()));
        v.put("presupuesto_fecha", p.getFecha() != null ? p.getFecha().format(FMT_DATETIME) : "");
        v.put("factura_numero", "");
        v.put("factura_fecha", "");
        v.put("remito_numero", "");
        v.put("remito_fecha", "");
        v.put("vendedor_nombre", "—");
        v.put("orden_compra", "—");
        v.put("plazos_cobranza", nz(p.getCliente() != null ? p.getCliente().getCondicionPago() : null));
        v.put("fecha_entrega", "—");
        v.put("direccion_entrega", direccionCliente(p.getCliente()));
        v.put("observaciones_texto", nz(p.getNotas()));
        v.put("forma_pago", nz(p.getCliente() != null ? p.getCliente().getCondicionPago() : null));
        v.put("tasa_financiacion", "A convenir");
        v.put("plazo_entrega", "A coordinar");
        v.put("garantia", "Según fabricante");
        v.put("presupuesto_ref", "—");
        v.put("vigencia", p.getValidezHasta() != null ? p.getValidezHasta().format(FMT_DATE) : "15 días");
        v.put("seccion_cronograma", "");

        BigDecimal sub = nzBd(p.getSubtotal());
        BigDecimal iva = nzBd(p.getIva());
        BigDecimal total = nzBd(p.getTotal());
        v.put("total_subtotal", money(sub));
        v.put("total_bonificacion", "$ 0,00");
        v.put("total_subtotal_neto", money(sub));
        v.put("filas_totales_extra", iva.compareTo(BigDecimal.ZERO) > 0
                ? "<div><span>IVA 21%</span><span>" + money(iva) + "</span></div>" : "");
        v.put("total_final", money(total));
        v.put("total_en_letras", NumeroALetrasUtil.pesos(total));

        putLineasPresupuesto(v, p.getLineas());
        return v;
    }

    private Map<String, String> varsRemito(Remito r) {
        Map<String, String> v = baseEmpresaVars();
        putCliente(v, r.getCliente(), null, null);
        v.put("remito_numero", nz(r.getNumeroRemito()));
        v.put("remito_fecha", r.getFecha() != null ? r.getFecha().format(FMT_DATETIME) : "");
        v.put("factura_numero", "");
        v.put("factura_fecha", "");
        v.put("presupuesto_numero", "");
        v.put("presupuesto_fecha", "");
        v.put("vendedor_nombre", "—");
        v.put("orden_compra", "—");
        v.put("plazos_cobranza", "—");
        v.put("fecha_entrega", r.getFecha() != null ? r.getFecha().format(FMT_DATE) : "—");
        v.put("direccion_entrega", nz(r.getDireccionEntrega() != null ? r.getDireccionEntrega() : direccionCliente(r.getCliente())));
        v.put("observaciones_texto", nz(r.getNotas()));
        v.put("forma_pago", "—");
        v.put("tasa_financiacion", "—");
        v.put("plazo_entrega", "—");
        v.put("garantia", "—");
        v.put("presupuesto_ref", r.getPresupuesto() != null ? nz(r.getPresupuesto().getNumeroPresupuesto()) : "—");
        v.put("vigencia", "—");
        v.put("total_subtotal", "—");
        v.put("total_bonificacion", "—");
        v.put("total_subtotal_neto", "—");
        v.put("filas_totales_extra", "");
        v.put("total_final", "—");
        v.put("total_en_letras", "—");
        v.put("seccion_cronograma", "");

        putLineasRemito(v, r.getLineas());
        return v;
    }

    private Map<String, String> baseEmpresaVars() {
        Map<String, String> v = new LinkedHashMap<>();
        Emisor e = emisorRepository.findByEsDefaultTrue().orElse(null);
        v.put("empresa_nombre", e != null ? nz(e.getRazonSocial()) : "NovaTech Store S.A.");
        v.put("empresa_direccion", e != null ? nz(e.getDomicilio()) : "Av. Corrientes 1234, CABA");
        v.put("empresa_telefono", "011-4567-8900");
        v.put("empresa_email", "ventas@novatech.com");
        v.put("empresa_cuit", e != null ? nz(e.getCuit()) : "30-71234567-8");
        v.put("empresa_ingresos_brutos", e != null ? nz(e.getIibb()) : "901-234567-8");
        v.put("empresa_inicio_actividades", "01/01/2020");
        v.put("empresa_condicion_iva", labelIva(e != null ? e.getCondicionIva() : null));
        return v;
    }

    private void putCliente(Map<String, String> v, PerfilCliente c, String cuitOverride, String ivaOverride) {
        if (c == null) {
            v.put("cliente_nombre", "Consumidor final");
            v.put("cliente_direccion", "—");
            v.put("cliente_cuit", nz(cuitOverride));
            v.put("cliente_condicion_iva", labelIva(ivaOverride));
            return;
        }
        v.put("cliente_nombre", c.getUsuario() != null ? nz(c.getUsuario().getNombre()) : "Cliente");
        v.put("cliente_direccion", direccionCliente(c));
        v.put("cliente_cuit", nz(cuitOverride != null ? cuitOverride : c.getCuit()));
        v.put("cliente_condicion_iva", labelIva(ivaOverride != null ? ivaOverride : c.getCondicionIva()));
    }

    private PerfilCliente clienteFactura(Factura f) {
        if (f.getPresupuesto() != null && f.getPresupuesto().getCliente() != null) {
            return f.getPresupuesto().getCliente();
        }
        if (f.getPedido() != null && f.getPedido().getUsuario() != null) {
            // pedido no tiene PerfilCliente directo — nombre desde usuario
            return null;
        }
        return null;
    }

    private void putLineas(Map<String, String> v, List<DetalleFactura> lineas, boolean sinPrecio) {
        for (int i = 1; i <= 20; i++) {
            v.put("item" + i + "_codigo", "");
            v.put("item" + i + "_descripcion_corta", "");
            v.put("item" + i + "_descripcion_larga", "");
            v.put("item" + i + "_cantidad", "");
            v.put("item" + i + "_precio", "");
            v.put("item" + i + "_subtotal", "");
        }
        if (lineas == null) {
            return;
        }
        int i = 1;
        for (DetalleFactura d : lineas) {
            if (i > 20) {
                break;
            }
            Producto prod = d.getProducto();
            v.put("item" + i + "_codigo", codigoProducto(prod));
            v.put("item" + i + "_descripcion_corta", prod != null ? nz(prod.getNombre()) : nz(d.getDescripcion()));
            v.put("item" + i + "_descripcion_larga", prod != null ? nz(prod.getDescripcion()) : nz(d.getDescripcion()));
            v.put("item" + i + "_cantidad", d.getCantidad() != null ? String.valueOf(d.getCantidad()) : "");
            v.put("item" + i + "_precio", sinPrecio ? "" : money(d.getPrecioUnitario()));
            v.put("item" + i + "_subtotal", sinPrecio ? "" : money(d.getSubtotal()));
            i++;
        }
    }

    private void putLineasPresupuesto(Map<String, String> v, List<DetallePresupuesto> lineas) {
        for (int i = 1; i <= 20; i++) {
            v.put("item" + i + "_codigo", "");
            v.put("item" + i + "_descripcion_corta", "");
            v.put("item" + i + "_descripcion_larga", "");
            v.put("item" + i + "_cantidad", "");
            v.put("item" + i + "_precio", "");
            v.put("item" + i + "_subtotal", "");
        }
        if (lineas == null) {
            return;
        }
        int i = 1;
        for (DetallePresupuesto d : lineas) {
            if (i > 20) {
                break;
            }
            Producto prod = d.getProducto();
            v.put("item" + i + "_codigo", codigoProducto(prod));
            v.put("item" + i + "_descripcion_corta", prod != null ? nz(prod.getNombre()) : "");
            v.put("item" + i + "_descripcion_larga", prod != null ? nz(prod.getDescripcion()) : "");
            v.put("item" + i + "_cantidad", d.getCantidad() != null ? String.valueOf(d.getCantidad()) : "");
            v.put("item" + i + "_precio", money(d.getPrecioUnitario()));
            v.put("item" + i + "_subtotal", money(d.getSubtotal()));
            i++;
        }
    }

    private void putLineasRemito(Map<String, String> v, List<DetalleRemito> lineas) {
        for (int i = 1; i <= 20; i++) {
            v.put("item" + i + "_codigo", "");
            v.put("item" + i + "_descripcion_corta", "");
            v.put("item" + i + "_descripcion_larga", "");
            v.put("item" + i + "_cantidad", "");
            v.put("item" + i + "_precio", "");
            v.put("item" + i + "_subtotal", "");
        }
        if (lineas == null) {
            return;
        }
        int i = 1;
        for (DetalleRemito d : lineas) {
            if (i > 20) {
                break;
            }
            Producto prod = d.getProducto();
            v.put("item" + i + "_codigo", codigoProducto(prod));
            v.put("item" + i + "_descripcion_corta", nz(d.getDescripcion() != null ? d.getDescripcion() : (prod != null ? prod.getNombre() : "")));
            v.put("item" + i + "_descripcion_larga", prod != null ? nz(prod.getDescripcion()) : nz(d.getDescripcion()));
            v.put("item" + i + "_cantidad", d.getCantidad() != null ? String.valueOf(d.getCantidad()) : "");
            i++;
        }
    }

    private void demoItems(Map<String, String> v, int count) {
        for (int i = 1; i <= 20; i++) {
            if (i <= count) {
                v.put("item" + i + "_codigo", "P-00" + i);
                v.put("item" + i + "_descripcion_corta", "Producto demo " + i);
                v.put("item" + i + "_descripcion_larga", "Descripción extendida del producto " + i);
                v.put("item" + i + "_cantidad", String.valueOf(i));
                v.put("item" + i + "_precio", "$ " + (10000 * i) + ",00");
                v.put("item" + i + "_subtotal", "$ " + (10000 * i * i) + ",00");
            } else {
                v.put("item" + i + "_codigo", "");
                v.put("item" + i + "_descripcion_corta", "");
                v.put("item" + i + "_descripcion_larga", "");
                v.put("item" + i + "_cantidad", "");
                v.put("item" + i + "_precio", "");
                v.put("item" + i + "_subtotal", "");
            }
        }
    }

    private String cronogramaFactura(Factura f) {
        if (f.getPedido() == null || f.getPedido().getIdPedido() == null) {
            return "";
        }
        List<PlanCuotas> planes = planRepository.findByPedidoIdPedido(f.getPedido().getIdPedido());
        if (planes.isEmpty()) {
            return "";
        }
        PlanCuotas plan = planes.get(0);
        List<Cuota> cuotas = cuotaRepository.findByPlanIdPlan(plan.getIdPlan());
        if (cuotas.isEmpty()) {
            return "";
        }
        List<String[]> rows = new ArrayList<>();
        for (Cuota c : cuotas) {
            rows.add(new String[] {
                    String.valueOf(c.getNumeroCuota()),
                    c.getFechaVencimiento() != null ? c.getFechaVencimiento().format(FMT_DATE) : "—",
                    money(c.getMonto()),
                    nz(c.getEstado())
            });
        }
        return cronogramaHtml(rows);
    }

    private String cronogramaHtml(List<String[]> rows) {
        if (rows == null || rows.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder("""
                <div class="doc-cronograma">
                  <p><strong>Cronograma de cuotas</strong></p>
                  <table><thead><tr><th>Cuota</th><th>Vencimiento</th><th>Monto</th><th>Estado</th></tr></thead><tbody>
                """);
        for (String[] r : rows) {
            sb.append("<tr><td>").append(safe(r[0])).append("</td><td>")
                    .append(safe(r[1])).append("</td><td>")
                    .append(safe(r[2])).append("</td><td>")
                    .append(safe(r[3])).append("</td></tr>");
        }
        sb.append("</tbody></table></div>");
        return sb.toString();
    }

    private String[] cuotaDemo(int n, String monto) {
        return new String[] { String.valueOf(n), LocalDate.now().plusMonths(n).withDayOfMonth(10).format(FMT_DATE),
                "$ " + monto, "PENDIENTE" };
    }

    private String codigoProducto(Producto p) {
        if (p == null) {
            return "";
        }
        if (p.getListaPrecioCodigo() != null && !p.getListaPrecioCodigo().isBlank()) {
            return p.getListaPrecioCodigo();
        }
        return p.getIdProducto() != null ? "P-" + p.getIdProducto() : "";
    }

    private String direccionCliente(PerfilCliente c) {
        if (c == null) {
            return "—";
        }
        String dir = nz(c.getDireccion());
        if (c.getCiudad() != null && !c.getCiudad().isBlank()) {
            dir = dir.equals("—") ? c.getCiudad() : dir + ", " + c.getCiudad();
        }
        return dir;
    }

    private String labelFormaCobro(Factura f) {
        if ("PRESTAMO_PERSONAL".equals(f.getFormaCobro())) {
            return "Préstamo personal — " + (f.getCantidadCuotas() != null ? f.getCantidadCuotas() : "—") + " cuotas";
        }
        return "Contado";
    }

    private String labelIva(String raw) {
        if (raw == null || raw.isBlank()) {
            return "—";
        }
        return raw.replace('_', ' ').toLowerCase(Locale.ROOT);
    }

    private String money(BigDecimal v) {
        if (v == null) {
            return "$ 0,00";
        }
        return "$ " + v.setScale(2, RoundingMode.HALF_UP).toString().replace('.', ',');
    }

    private BigDecimal nzBd(BigDecimal v) {
        return v != null ? v : BigDecimal.ZERO;
    }

    private String nz(String s) {
        return s != null && !s.isBlank() ? s : "—";
    }

    private String safe(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
