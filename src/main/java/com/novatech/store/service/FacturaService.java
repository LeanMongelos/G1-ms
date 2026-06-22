package com.novatech.store.service;

import com.novatech.store.config.FacturacionConfig;
import com.novatech.store.dto.GenerarFacturaRequest;
import com.novatech.store.dto.LineaComprobanteDto;
import com.novatech.store.entity.DetalleFactura;
import com.novatech.store.entity.DetallePedido;
import com.novatech.store.entity.DetallePresupuesto;
import com.novatech.store.entity.Factura;
import com.novatech.store.entity.Pedido;
import com.novatech.store.entity.PerfilCliente;
import com.novatech.store.entity.PlanCuotas;
import com.novatech.store.entity.Presupuesto;
import com.novatech.store.entity.Producto;
import com.novatech.store.entity.Remito;
import com.novatech.store.exception.ReglaNegocioException;
import com.novatech.store.exception.ResourceNotFoundException;
import com.novatech.store.repository.DetallePedidoRepository;
import com.novatech.store.repository.FacturaRepository;
import com.novatech.store.repository.PedidoRepository;
import com.novatech.store.repository.PerfilClienteRepository;
import com.novatech.store.repository.PlanCuotasRepository;
import com.novatech.store.repository.PresupuestoRepository;
import com.novatech.store.repository.ProductoRepository;
import com.novatech.store.repository.RemitoRepository;
import com.novatech.store.util.ComprobanteCalculoUtil;
import com.novatech.store.util.ComprobanteCalculoUtil.TotalesComprobante;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FacturaService {

    private static final BigDecimal IVA_RATE = ComprobanteCalculoUtil.IVA_DEFAULT_RATE;

    private final FacturaRepository repository;
    private final PedidoRepository pedidoRepository;
    private final PresupuestoRepository presupuestoRepository;
    private final RemitoRepository remitoRepository;
    private final PerfilClienteRepository perfilRepository;
    private final PlanCuotasRepository planRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final ProductoRepository productoRepository;
    private final PlanCuotasService planCuotasService;

    public FacturaService(FacturaRepository repository,
                          PedidoRepository pedidoRepository,
                          PresupuestoRepository presupuestoRepository,
                          RemitoRepository remitoRepository,
                          PerfilClienteRepository perfilRepository,
                          PlanCuotasRepository planRepository,
                          DetallePedidoRepository detallePedidoRepository,
                          ProductoRepository productoRepository,
                          PlanCuotasService planCuotasService) {
        this.repository = repository;
        this.pedidoRepository = pedidoRepository;
        this.presupuestoRepository = presupuestoRepository;
        this.remitoRepository = remitoRepository;
        this.perfilRepository = perfilRepository;
        this.planRepository = planRepository;
        this.detallePedidoRepository = detallePedidoRepository;
        this.productoRepository = productoRepository;
        this.planCuotasService = planCuotasService;
    }

    public List<Factura> listar() {
        return repository.findAll();
    }

    public Factura obtener(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Factura no encontrada: " + id));
    }

    public Factura crear(Factura factura) {
        factura.setIdFactura(null);
        if (factura.getEstado() == null || factura.getEstado().isBlank()) {
            factura.setEstado("BORRADOR");
        }
        vincularReferencias(factura);
        return repository.save(factura);
    }

    @Transactional
    public Factura generar(GenerarFacturaRequest request) {
        if (request.getPresupuestoId() != null) {
            return generarDesdePresupuesto(request.getPresupuestoId(), request);
        }
        if (request.getPedidoId() != null) {
            return generarDesdePedido(request);
        }
        if (request.getClienteId() != null && request.getLineas() != null && !request.getLineas().isEmpty()) {
            return generarManual(request);
        }
        throw new ReglaNegocioException("Debe indicar pedidoId, presupuestoId o cliente con líneas.");
    }

    @Transactional
    public Factura generarManual(GenerarFacturaRequest request) {
        if (request.getClienteId() == null) {
            throw new ReglaNegocioException("Debe indicar el cliente.");
        }
        if (request.getLineas() == null || request.getLineas().isEmpty()) {
            throw new ReglaNegocioException("Agregá al menos una línea a la factura.");
        }

        PerfilCliente cliente = perfilRepository.findById(request.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado: " + request.getClienteId()));
        String condicionIva = cliente.getCondicionIva();
        String tipoComprobante = request.getTipoComprobante() != null ? request.getTipoComprobante()
                : tipoComprobanteParaCliente(condicionIva);
        BigDecimal tasa = ComprobanteCalculoUtil.tasaIvaParaCondicion(condicionIva);

        List<DetalleFactura> lineas = new ArrayList<>();
        BigDecimal subtotalAcum = BigDecimal.ZERO;
        for (LineaComprobanteDto dto : request.getLineas()) {
            if (dto.getIdProducto() == null || dto.getCantidad() == null || dto.getCantidad() < 1) {
                throw new ReglaNegocioException("Cada línea debe tener producto y cantidad válida.");
            }
            Producto producto = productoRepository.findById(dto.getIdProducto())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + dto.getIdProducto()));
            BigDecimal precio = dto.getPrecioUnitario() != null ? dto.getPrecioUnitario() : producto.getPrecio();
            BigDecimal descuento = dto.getDescuentoPorcentaje() != null ? dto.getDescuentoPorcentaje() : BigDecimal.ZERO;
            BigDecimal subtotalLinea = ComprobanteCalculoUtil.calcularSubtotalLinea(dto.getCantidad(), precio, descuento);

            DetalleFactura det = new DetalleFactura();
            det.setProducto(producto);
            det.setCantidad(dto.getCantidad());
            det.setPrecioUnitario(precio);
            det.setDescuentoPorcentaje(descuento);
            det.setSubtotal(subtotalLinea);
            det.setDescripcion(dto.getDescripcion() != null ? dto.getDescripcion() : producto.getNombre());
            lineas.add(det);
            subtotalAcum = subtotalAcum.add(subtotalLinea);
        }

        TotalesComprobante totales = ComprobanteCalculoUtil.calcularTotales(subtotalAcum, tasa);

        Factura factura = new Factura();
        factura.setNumeroFactura(generarNumero());
        factura.setPuntoVenta(request.getPuntoVenta() != null ? request.getPuntoVenta() : 1);
        factura.setFechaEmision(LocalDateTime.now());
        factura.setSubtotal(totales.subtotal());
        factura.setIva(totales.iva());
        factura.setTotal(totales.total());
        factura.setEstado("EMITIDA");
        factura.setTipoComprobante(tipoComprobante);
        factura.setCondicionIvaCliente(condicionIva);
        if (cliente.getCuit() != null) {
            factura.setCuitCliente(cliente.getCuit());
        }
        factura.setCae("PENDIENTE-AFIP");
        factura.setCaeVencimiento(LocalDate.now().plusDays(10));
        factura.setFormaCobro(request.getFormaCobro() != null ? request.getFormaCobro() : "CONTADO");
        factura.setNotas(request.getNotas());

        for (DetalleFactura det : lineas) {
            det.setFactura(factura);
        }
        factura.setLineas(lineas);
        return repository.save(factura);
    }

    @Transactional
    public Factura generarDesdePedido(GenerarFacturaRequest request) {
        if (request.getPedidoId() == null) {
            throw new ReglaNegocioException("Debe indicar el pedido.");
        }
        return generarDesdePedido(request.getPedidoId(), request);
    }

    @Transactional
    public Factura generarDesdePedido(Integer idPedido) {
        GenerarFacturaRequest req = new GenerarFacturaRequest();
        req.setPedidoId(idPedido);
        req.setFormaCobro("CONTADO");
        return generarDesdePedido(idPedido, req);
    }

    @Transactional
    public Factura generarDesdePresupuesto(Integer idPresupuesto) {
        GenerarFacturaRequest req = new GenerarFacturaRequest();
        req.setPresupuestoId(idPresupuesto);
        req.setFormaCobro("CONTADO");
        return generarDesdePresupuesto(idPresupuesto, req);
    }

    @Transactional
    public Factura generarDesdePresupuesto(Integer idPresupuesto, GenerarFacturaRequest request) {
        Presupuesto presupuesto = presupuestoRepository.findById(idPresupuesto)
                .orElseThrow(() -> new ResourceNotFoundException("Presupuesto no encontrado: " + idPresupuesto));

        validarPresupuestoFacturable(presupuesto);

        List<Factura> existentes = repository.findByPresupuestoIdPresupuesto(idPresupuesto);
        boolean yaEmitida = existentes.stream().anyMatch(f -> "EMITIDA".equals(f.getEstado()));
        if (yaEmitida) {
            throw new ReglaNegocioException("Ya existe una factura emitida para este presupuesto.");
        }

        PerfilCliente cliente = presupuesto.getCliente();
        String condicionIva = cliente != null ? cliente.getCondicionIva() : null;
        String tipoComprobante = request.getTipoComprobante() != null ? request.getTipoComprobante()
                : tipoComprobanteParaCliente(condicionIva);

        Factura factura = new Factura();
        factura.setPresupuesto(presupuesto);
        factura.setNumeroFactura(generarNumero());
        factura.setPuntoVenta(request.getPuntoVenta() != null ? request.getPuntoVenta() : 1);
        factura.setFechaEmision(LocalDateTime.now());
        factura.setSubtotal(presupuesto.getSubtotal());
        factura.setIva(presupuesto.getIva());
        factura.setTotal(presupuesto.getTotal());
        factura.setEstado("EMITIDA");
        factura.setTipoComprobante(tipoComprobante);
        factura.setCondicionIvaCliente(condicionIva);
        if (cliente != null && cliente.getCuit() != null) {
            factura.setCuitCliente(cliente.getCuit());
        }
        factura.setCae("PENDIENTE-AFIP");
        factura.setCaeVencimiento(LocalDate.now().plusDays(10));
        factura.setFormaCobro(request.getFormaCobro() != null ? request.getFormaCobro() : "CONTADO");
        factura.setNotas(request.getNotas() != null ? request.getNotas() : presupuesto.getNotas());

        if (request.getRemitoId() != null) {
            Remito remito = remitoRepository.findById(request.getRemitoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Remito no encontrado: " + request.getRemitoId()));
            factura.setRemito(remito);
        }

        copiarLineasDesdePresupuesto(factura, presupuesto);

        Factura guardada = repository.save(factura);
        presupuesto.setEstado(FacturacionConfig.ESTADO_PRESUPUESTO_FACTURADO);
        presupuestoRepository.save(presupuesto);
        return guardada;
    }

    private Factura generarDesdePedido(Integer idPedido, GenerarFacturaRequest request) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado: " + idPedido));

        List<Factura> existentes = repository.findByPedidoIdPedido(idPedido);
        boolean yaEmitida = existentes.stream().anyMatch(f -> "EMITIDA".equals(f.getEstado()));
        if (yaEmitida) {
            throw new ReglaNegocioException("Ya existe una factura emitida para este pedido.");
        }
        validarPedidoFacturable(pedido, idPedido);

        PerfilCliente perfil = null;
        if (pedido.getUsuario() != null) {
            perfil = perfilRepository.findByUsuario_IdUsuario(pedido.getUsuario().getIdUsuario()).orElse(null);
        }
        String condicionIva = perfil != null ? perfil.getCondicionIva() : null;
        BigDecimal tasa = ComprobanteCalculoUtil.tasaIvaParaCondicion(condicionIva);
        BigDecimal total = pedido.getTotal();
        BigDecimal subtotal = total.divide(BigDecimal.ONE.add(tasa), 2, RoundingMode.HALF_UP);
        BigDecimal iva = total.subtract(subtotal);

        Factura factura = new Factura();
        factura.setPedido(pedido);
        factura.setNumeroFactura(generarNumero());
        factura.setPuntoVenta(request.getPuntoVenta() != null ? request.getPuntoVenta() : 1);
        factura.setFechaEmision(LocalDateTime.now());
        factura.setSubtotal(subtotal);
        factura.setIva(iva);
        factura.setTotal(total);
        factura.setEstado("EMITIDA");
        factura.setTipoComprobante(request.getTipoComprobante() != null ? request.getTipoComprobante()
                : tipoComprobanteParaCliente(condicionIva));
        factura.setCondicionIvaCliente(condicionIva);
        if (perfil != null && perfil.getCuit() != null) {
            factura.setCuitCliente(perfil.getCuit());
        }
        factura.setCae("PENDIENTE-AFIP");
        factura.setCaeVencimiento(LocalDate.now().plusDays(10));

        if (request.getRemitoId() != null) {
            Remito remito = remitoRepository.findById(request.getRemitoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Remito no encontrado: " + request.getRemitoId()));
            factura.setRemito(remito);
        }

        String forma = request.getFormaCobro() != null ? request.getFormaCobro() : "CONTADO";
        if ("PRESTAMO_PERSONAL".equalsIgnoreCase(forma)) {
            int cuotas = request.getCantidadCuotas() != null ? request.getCantidadCuotas() : 6;
            if (cuotas < 1 || cuotas > 24) {
                throw new ReglaNegocioException("Cantidad de cuotas inválida (1-24).");
            }
            if (request.getInteres() == null) {
                throw new ReglaNegocioException("Debe indicar el interés del préstamo (%).");
            }
            BigDecimal interes = request.getInteres();
            if (interes.compareTo(BigDecimal.ZERO) < 0 || interes.compareTo(new BigDecimal("100")) > 0) {
                throw new ReglaNegocioException("Interés inválido (0-100%).");
            }
            crearPlanPrestamo(pedido, cuotas, interes);
            factura.setFormaCobro("PRESTAMO_PERSONAL");
            factura.setCantidadCuotas(cuotas);
        } else {
            factura.setFormaCobro("CONTADO");
        }

        copiarLineasDesdePedido(factura, idPedido);
        return repository.save(factura);
    }

    private void validarPresupuestoFacturable(Presupuesto presupuesto) {
        if ("FACTURADO".equalsIgnoreCase(presupuesto.getEstado())) {
            throw new ReglaNegocioException("El presupuesto ya fue facturado.");
        }
        String estado = presupuesto.getEstado() != null ? presupuesto.getEstado().toUpperCase() : "";
        if (!FacturacionConfig.ESTADOS_PRESUPUESTO_FACTURABLES.contains(estado)) {
            throw new ReglaNegocioException(
                    "Solo se puede facturar un presupuesto ENVIADO o APROBADO (estado actual: "
                            + presupuesto.getEstado() + ").");
        }
    }

    private String tipoComprobanteParaCliente(String condicionIva) {
        if (condicionIva == null) {
            return "FACTURA_B";
        }
        return switch (condicionIva.toUpperCase()) {
            case "RESPONSABLE_INSCRIPTO" -> "FACTURA_A";
            case "MONOTRIBUTO" -> "FACTURA_C";
            default -> "FACTURA_B";
        };
    }

    private void copiarLineasDesdePresupuesto(Factura factura, Presupuesto presupuesto) {
        List<DetalleFactura> lineas = new ArrayList<>();
        for (DetallePresupuesto src : presupuesto.getLineas()) {
            DetalleFactura det = new DetalleFactura();
            det.setFactura(factura);
            det.setProducto(src.getProducto());
            det.setCantidad(src.getCantidad());
            det.setPrecioUnitario(src.getPrecioUnitario());
            det.setDescuentoPorcentaje(src.getDescuentoPorcentaje());
            det.setSubtotal(src.getSubtotal());
            if (src.getProducto() != null) {
                det.setDescripcion(src.getProducto().getNombre());
            }
            lineas.add(det);
        }
        factura.setLineas(lineas);
    }

    private void copiarLineasDesdePedido(Factura factura, Integer idPedido) {
        List<DetallePedido> detalles = detallePedidoRepository.findByPedidoIdPedido(idPedido);
        List<DetalleFactura> lineas = new ArrayList<>();
        for (DetallePedido src : detalles) {
            BigDecimal subtotal = src.getPrecioUnitario().multiply(BigDecimal.valueOf(src.getCantidad()));
            DetalleFactura det = new DetalleFactura();
            det.setFactura(factura);
            det.setProducto(src.getProducto());
            det.setCantidad(src.getCantidad());
            det.setPrecioUnitario(src.getPrecioUnitario());
            det.setDescuentoPorcentaje(BigDecimal.ZERO);
            det.setSubtotal(subtotal);
            if (src.getProducto() != null) {
                det.setDescripcion(src.getProducto().getNombre());
            }
            lineas.add(det);
        }
        factura.setLineas(lineas);
    }

    private void crearPlanPrestamo(Pedido pedido, int cuotas, BigDecimal interes) {
        if (pedido.getUsuario() == null || pedido.getUsuario().getIdUsuario() == null) {
            throw new ReglaNegocioException("El pedido no tiene usuario asociado.");
        }
        List<PlanCuotas> existentes = planRepository.findByPedidoIdPedido(pedido.getIdPedido());
        boolean planActivo = existentes.stream().anyMatch(p -> "ACTIVO".equals(p.getEstado()));
        if (planActivo) {
            throw new ReglaNegocioException("Ya existe un plan de cuotas activo para este pedido.");
        }

        PerfilCliente perfil = perfilRepository.findByUsuario_IdUsuario(pedido.getUsuario().getIdUsuario())
                .orElseThrow(() -> new ReglaNegocioException(
                        "El cliente no tiene perfil. Creá el perfil antes de facturar con préstamo."));

        PlanCuotas plan = new PlanCuotas();
        plan.setPedido(pedido);
        plan.setCliente(perfil);
        plan.setCantidadCuotas(cuotas);
        plan.setInteres(interes);
        plan.setEstado("ACTIVO");
        planCuotasService.crear(plan);
    }

    public Factura actualizar(Integer id, Factura datos) {
        Factura factura = obtener(id);
        if ("EMITIDA".equals(factura.getEstado()) && "ANULADA".equals(datos.getEstado())) {
            factura.setEstado("ANULADA");
            return repository.save(factura);
        }
        if ("EMITIDA".equals(factura.getEstado())) {
            throw new ReglaNegocioException("Solo se puede anular una factura emitida.");
        }
        factura.setPedido(datos.getPedido());
        factura.setPresupuesto(datos.getPresupuesto());
        factura.setRemito(datos.getRemito());
        factura.setSubtotal(datos.getSubtotal());
        factura.setIva(datos.getIva());
        factura.setTotal(datos.getTotal());
        factura.setEstado(datos.getEstado());
        factura.setTipoComprobante(datos.getTipoComprobante());
        factura.setCuitCliente(datos.getCuitCliente());
        factura.setCondicionIvaCliente(datos.getCondicionIvaCliente());
        factura.setPuntoVenta(datos.getPuntoVenta());
        factura.setNotas(datos.getNotas());
        vincularReferencias(factura);
        return repository.save(factura);
    }

    public Factura emitir(Integer id) {
        Factura factura = obtener(id);
        if ("EMITIDA".equals(factura.getEstado())) {
            throw new ReglaNegocioException("La factura ya está emitida.");
        }
        if (factura.getNumeroFactura() == null || factura.getNumeroFactura().isBlank()) {
            factura.setNumeroFactura(generarNumero());
        }
        factura.setEstado("EMITIDA");
        factura.setFechaEmision(LocalDateTime.now());
        return repository.save(factura);
    }

    public void eliminar(Integer id) {
        Factura factura = obtener(id);
        if ("EMITIDA".equals(factura.getEstado())) {
            throw new ReglaNegocioException("No se puede eliminar una factura emitida. Anúlela.");
        }
        repository.deleteById(id);
    }

    private void validarPedidoFacturable(Pedido pedido, Integer idPedido) {
        if ("PAGADO".equalsIgnoreCase(pedido.getEstado())) {
            return;
        }
        List<PlanCuotas> planes = planRepository.findByPedidoIdPedido(idPedido);
        boolean planActivo = planes.stream()
                .anyMatch(p -> p.getEstado() != null && "ACTIVO".equalsIgnoreCase(p.getEstado()));
        if (planActivo) {
            return;
        }
        throw new ReglaNegocioException(
                "No se puede facturar el pedido: debe estar PAGADO o tener un plan de cuotas ACTIVO.");
    }

    private void vincularReferencias(Factura factura) {
        if (factura.getPedido() != null && factura.getPedido().getIdPedido() != null) {
            Pedido pedido = pedidoRepository.findById(factura.getPedido().getIdPedido())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Pedido no encontrado: " + factura.getPedido().getIdPedido()));
            factura.setPedido(pedido);
        }
        if (factura.getPresupuesto() != null && factura.getPresupuesto().getIdPresupuesto() != null) {
            Presupuesto presupuesto = presupuestoRepository.findById(factura.getPresupuesto().getIdPresupuesto())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Presupuesto no encontrado: " + factura.getPresupuesto().getIdPresupuesto()));
            factura.setPresupuesto(presupuesto);
        }
        if (factura.getRemito() != null && factura.getRemito().getIdRemito() != null) {
            Remito remito = remitoRepository.findById(factura.getRemito().getIdRemito())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Remito no encontrado: " + factura.getRemito().getIdRemito()));
            factura.setRemito(remito);
        }
    }

    private String generarNumero() {
        long count = repository.count() + 1;
        return "NV-" + Year.now().getValue() + "-" + String.format("%06d", count);
    }
}
