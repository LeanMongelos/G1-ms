package com.novatech.store.service;

import com.novatech.store.dto.ConfirmarOrdenLineaRequest;
import com.novatech.store.dto.ConfirmarOrdenRequest;
import com.novatech.store.dto.ConfirmarOrdenResponse;
import com.novatech.store.entity.DetallePedido;
import com.novatech.store.entity.Envio;
import com.novatech.store.entity.Pago;
import com.novatech.store.entity.Pedido;
import com.novatech.store.entity.PlanCuotas;
import com.novatech.store.entity.Producto;
import com.novatech.store.entity.Usuario;
import com.novatech.store.exception.ReglaNegocioException;
import com.novatech.store.exception.ResourceNotFoundException;
import com.novatech.store.repository.DetallePedidoRepository;
import com.novatech.store.repository.PagoRepository;
import com.novatech.store.repository.PedidoRepository;
import com.novatech.store.repository.PerfilClienteRepository;
import com.novatech.store.repository.ProductoRepository;
import com.novatech.store.repository.UsuarioRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio `OrdenVentaService`: reglas de negocio, transacciones y orquestación de OrdenVenta. Los controllers delegan aquí; no accede HTTP directamente.
 */
@Service
public class OrdenVentaService {

    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final ProductoRepository productoRepository;
    private final PagoRepository pagoRepository;
    private final EnvioService envioService;
    private final PlanCuotasService planCuotasService;
    private final UsuarioRepository usuarioRepository;
    private final PerfilClienteRepository perfilClienteRepository;
    private final ListaPrecioService listaPrecioService;

    public OrdenVentaService(PedidoRepository pedidoRepository,
                             DetallePedidoRepository detallePedidoRepository,
                             ProductoRepository productoRepository,
                             PagoRepository pagoRepository,
                             EnvioService envioService,
                             PlanCuotasService planCuotasService,
                             UsuarioRepository usuarioRepository,
                             PerfilClienteRepository perfilClienteRepository,
                             ListaPrecioService listaPrecioService) {
        this.pedidoRepository = pedidoRepository;
        this.detallePedidoRepository = detallePedidoRepository;
        this.productoRepository = productoRepository;
        this.pagoRepository = pagoRepository;
        this.envioService = envioService;
        this.planCuotasService = planCuotasService;
        this.usuarioRepository = usuarioRepository;
        this.perfilClienteRepository = perfilClienteRepository;
        this.listaPrecioService = listaPrecioService;
    }

    @Transactional
    public ConfirmarOrdenResponse confirmar(ConfirmarOrdenRequest request) {
        Usuario usuario = usuarioRepository.findById(request.getIdUsuario())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuario no encontrado: " + request.getIdUsuario()));

        String metodo = request.getMetodoPago().trim().toUpperCase();
        String tipoEntrega = normalizarTipoEntrega(request.getTipoEntrega());
        String canalOrigen = normalizarCanalOrigen(request.getCanalOrigen());

        String tipoCliente = perfilClienteRepository.findByUsuario_IdUsuario(usuario.getIdUsuario())
                .map(p -> p.getTipoCliente())
                .orElse(null);
        String codigoLista = request.getCodigoListaPrecio();
        if (codigoLista == null || codigoLista.isBlank()) {
            codigoLista = listaPrecioService.resolverCodigoLista(canalOrigen, tipoCliente);
        }

        if ("ENVIO".equals(tipoEntrega)
                && (request.getDireccionEnvio() == null || request.getDireccionEnvio().isBlank())) {
            throw new ReglaNegocioException("La direccion de envio es obligatoria para entrega a domicilio.");
        }

        List<DetallePedido> detalles = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        for (ConfirmarOrdenLineaRequest linea : request.getLineas()) {
            Producto producto = productoRepository.findById(linea.getIdProducto())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Producto no encontrado: " + linea.getIdProducto()));

            int cantidad = linea.getCantidad();
            if (producto.getStock() == null || producto.getStock() < cantidad) {
                throw new ReglaNegocioException(
                        "Stock insuficiente para \"" + producto.getNombre()
                                + "\". Disponible: " + (producto.getStock() == null ? 0 : producto.getStock())
                                + ", solicitado: " + cantidad + ".");
            }

            BigDecimal precio = listaPrecioService.resolverPrecio(producto, codigoLista).getPrecioEfectivo();
            subtotal = subtotal.add(precio.multiply(BigDecimal.valueOf(cantidad)));

            DetallePedido detalle = new DetallePedido();
            detalle.setProducto(producto);
            detalle.setCantidad(cantidad);
            detalle.setPrecioUnitario(precio);
            detalles.add(detalle);
        }

        if (subtotal.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ReglaNegocioException("El total del pedido debe ser mayor a cero.");
        }

        BigDecimal total = subtotal;
        if ("PRESTAMO_CASA".equals(metodo)) {
            total = aplicarInteres(subtotal, request.getInteres());
        }

        EstadoPagoResuelto estados = resolverEstados(metodo, canalOrigen);

        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setFecha(LocalDateTime.now());
        pedido.setTotal(total);
        pedido.setEstado(estados.estadoPedido());
        pedido.setCanalOrigen(canalOrigen);
        pedido.setTipoEntrega(tipoEntrega);
        pedido.setNotas(appendNotaLista(request.getNotas(), codigoLista));
        pedido = pedidoRepository.save(pedido);

        for (DetallePedido detalle : detalles) {
            detalle.setPedido(pedido);
            detallePedidoRepository.save(detalle);

            Producto producto = detalle.getProducto();
            producto.setStock(producto.getStock() - detalle.getCantidad());
            productoRepository.save(producto);
        }

        Pago pago = new Pago();
        pago.setPedido(pedido);
        pago.setFechaPago(LocalDateTime.now());
        pago.setMonto(total);
        pago.setMetodo(metodo);
        pago.setEstado(estados.estadoPago());
        pago.setProveedorBilletera(request.getProveedorBilletera());
        pago.setReferencia(request.getReferencia() != null && !request.getReferencia().isBlank()
                ? request.getReferencia()
                : generarReferencia(metodo));
        pago = pagoRepository.save(pago);

        Envio envio = null;
        if ("ENVIO".equals(tipoEntrega)) {
            Envio nuevoEnvio = new Envio();
            nuevoEnvio.setPedido(pedido);
            nuevoEnvio.setDireccionEnvio(request.getDireccionEnvio().trim());
            nuevoEnvio.setEmpresaLogistica(
                    request.getEmpresaLogistica() != null && !request.getEmpresaLogistica().isBlank()
                            ? request.getEmpresaLogistica()
                            : "Andreani");
            nuevoEnvio.setEstadoEnvio("PREPARANDO");
            envio = envioService.crear(nuevoEnvio);
        }

        PlanCuotas planCuotas = null;
        if ("PRESTAMO_CASA".equals(metodo)) {
            planCuotas = crearPlanCuotasSiCorresponde(request, pedido, usuario);
        }

        return new ConfirmarOrdenResponse(pedido, pago, envio, planCuotas);
    }

    private PlanCuotas crearPlanCuotasSiCorresponde(ConfirmarOrdenRequest request,
                                                    Pedido pedido,
                                                    Usuario usuario) {
        return perfilClienteRepository.findByUsuario_IdUsuario(usuario.getIdUsuario())
                .map(perfil -> {
                    PlanCuotas plan = new PlanCuotas();
                    plan.setCliente(perfil);
                    plan.setPedido(pedido);
                    plan.setCantidadCuotas(request.getCantidadCuotas() != null ? request.getCantidadCuotas() : 6);
                    plan.setInteres(request.getInteres() != null ? request.getInteres() : BigDecimal.ZERO);
                    plan.setEstado("ACTIVO");
                    return planCuotasService.crear(plan);
                })
                .orElse(null);
    }

    private BigDecimal aplicarInteres(BigDecimal subtotal, BigDecimal interes) {
        if (interes == null || interes.compareTo(BigDecimal.ZERO) <= 0) {
            return subtotal;
        }
        BigDecimal factor = BigDecimal.ONE.add(
                interes.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
        return subtotal.multiply(factor).setScale(2, RoundingMode.HALF_UP);
    }

    private EstadoPagoResuelto resolverEstados(String metodo, String canalOrigen) {
        if ("POS".equals(canalOrigen)) {
            return switch (metodo) {
                case "TARJETA", "EFECTIVO", "TRANSFERENCIA" ->
                        new EstadoPagoResuelto("PAGADO", "APROBADO");
                default -> throw new ReglaNegocioException(
                        "Metodo de pago no valido para POS: " + metodo);
            };
        }
        return switch (metodo) {
            case "TARJETA", "MERCADO_PAGO", "QR", "BILLETERA_VIRTUAL" ->
                    new EstadoPagoResuelto("PAGADO", "APROBADO");
            case "EFECTIVO", "TRANSFERENCIA", "CONTRA_ENTREGA", "PRESTAMO_CASA" ->
                    new EstadoPagoResuelto("PENDIENTE", "PENDIENTE");
            default -> throw new ReglaNegocioException("Metodo de pago no valido: " + metodo);
        };
    }

    private String generarReferencia(String metodo) {
        return metodo + "-" + System.currentTimeMillis();
    }

    private String normalizarTipoEntrega(String tipoEntrega) {
        if (tipoEntrega == null || tipoEntrega.isBlank()) {
            return "ENVIO";
        }
        String normalizado = tipoEntrega.trim().toUpperCase();
        if (!"ENVIO".equals(normalizado) && !"RETIRO_TIENDA".equals(normalizado)) {
            throw new ReglaNegocioException("Tipo de entrega invalido: " + tipoEntrega);
        }
        return normalizado;
    }

    private static final java.util.Set<String> CANALES_ORIGEN = java.util.Set.of(
            "WEB", "ADMIN", "WHATSAPP", "EMAIL", "INSTAGRAM", "FACEBOOK", "POS");

    private String normalizarCanalOrigen(String canalOrigen) {
        if (canalOrigen == null || canalOrigen.isBlank()) {
            return "WEB";
        }
        String normalizado = canalOrigen.trim().toUpperCase();
        if (!CANALES_ORIGEN.contains(normalizado)) {
            throw new ReglaNegocioException("Canal de origen invalido: " + canalOrigen);
        }
        return normalizado;
    }

    private String appendNotaLista(String notas, String codigoLista) {
        String tag = "Lista precio: " + codigoLista;
        if (notas == null || notas.isBlank()) {
            return tag;
        }
        if (notas.contains("Lista precio:")) {
            return notas;
        }
        return notas + " · " + tag;
    }

    private record EstadoPagoResuelto(String estadoPedido, String estadoPago) {
    }
}
