package com.novatech.store.service;

import com.novatech.store.entity.Pago;
import com.novatech.store.entity.Pedido;
import com.novatech.store.entity.Usuario;
import com.novatech.store.exception.AccesoDenegadoException;
import com.novatech.store.exception.ReglaNegocioException;
import com.novatech.store.exception.ResourceNotFoundException;
import com.novatech.store.repository.PagoRepository;
import com.novatech.store.repository.PedidoRepository;
import com.novatech.store.repository.UsuarioRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

// Logica de negocio para los pagos.
@Service
public class PagoService {

    private final PagoRepository repository;
    // Necesitamos los pedidos para validar montos y actualizar su estado.
    private final PedidoRepository pedidoRepository;
    // Necesitamos los usuarios para validar el rol de quien autoriza el pago.
    private final UsuarioRepository usuarioRepository;

    public PagoService(PagoRepository repository,
                       PedidoRepository pedidoRepository,
                       UsuarioRepository usuarioRepository) {
        this.repository = repository;
        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // Trae todos los pagos.
    public List<Pago> listar() {
        return repository.findAll();
    }

    // Trae un pago por su id. Si no existe, lanza error 404.
    public Pago obtener(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado: " + id));
    }

    // Crea un pago nuevo. Si no viene la fecha, ponemos la de ahora.
    public Pago crear(Pago pago) {
        pago.setIdPago(null);
        if (pago.getFechaPago() == null) {
            pago.setFechaPago(LocalDateTime.now());
        }
        // Si no viene estado, asumimos que el pago quedo aprobado.
        if (pago.getEstado() == null || pago.getEstado().isBlank()) {
            pago.setEstado("APROBADO");
        }

        // 1) Buscamos el pedido al que corresponde el pago. Tiene que venir y existir.
        if (pago.getPedido() == null || pago.getPedido().getIdPedido() == null) {
            throw new ReglaNegocioException("El pago debe indicar a que pedido corresponde (id_pedido).");
        }
        Pedido pedido = pedidoRepository.findById(pago.getPedido().getIdPedido())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Pedido no encontrado: " + pago.getPedido().getIdPedido()));
        // Usamos el pedido real de la base (no el que llego suelto en el JSON).
        pago.setPedido(pedido);

        // 2) Solo un ADMIN puede autorizar/aprobar un pago. Validamos el rol de quien aprueba.
        if (pago.getAprobadoPor() != null && pago.getAprobadoPor().getIdUsuario() != null) {
            Usuario aprobador = usuarioRepository.findById(pago.getAprobadoPor().getIdUsuario())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Usuario que aprueba no encontrado: " + pago.getAprobadoPor().getIdUsuario()));
            if (aprobador.getRol() == null || !aprobador.getRol().equalsIgnoreCase("ADMIN")) {
                throw new AccesoDenegadoException(
                        "Solo un usuario ADMIN puede autorizar un pago.");
            }
            pago.setAprobadoPor(aprobador);
        }

        // 3) El monto es obligatorio y no puede superar lo que falta pagar del pedido.
        if (pago.getMonto() == null) {
            throw new ReglaNegocioException("El monto del pago es obligatorio.");
        }
        BigDecimal total = pedido.getTotal() == null ? BigDecimal.ZERO : pedido.getTotal();
        // Sumamos lo que ya se pago y quedo APROBADO en pagos anteriores de este pedido.
        BigDecimal yaPagado = sumarPagosAprobados(pedido.getIdPedido());
        BigDecimal nuevoAcumulado = yaPagado.add(pago.getMonto());
        if (nuevoAcumulado.compareTo(total) > 0) {
            throw new ReglaNegocioException(
                    "El monto del pago supera el total del pedido. Total: " + total
                            + ", ya pagado: " + yaPagado + ", intento de pago: " + pago.getMonto() + ".");
        }

        // 4) Guardamos el pago.
        Pago guardado = repository.save(pago);

        // 5) Si el pago quedo APROBADO, actualizamos el estado del pedido.
        //    Si con este pago se cubre el total -> "PAGADO". Si todavia falta -> "PARCIAL".
        if ("APROBADO".equalsIgnoreCase(guardado.getEstado())) {
            if (nuevoAcumulado.compareTo(total) >= 0) {
                pedido.setEstado("PAGADO");
            } else {
                pedido.setEstado("PARCIAL");
            }
            pedidoRepository.save(pedido);
        }

        return guardado;
    }

    // Suma los montos de los pagos APROBADOS que ya tiene un pedido.
    private BigDecimal sumarPagosAprobados(Integer idPedido) {
        BigDecimal suma = BigDecimal.ZERO;
        for (Pago p : repository.findByPedidoIdPedido(idPedido)) {
            if (p.getMonto() != null && p.getEstado() != null
                    && p.getEstado().equalsIgnoreCase("APROBADO")) {
                suma = suma.add(p.getMonto());
            }
        }
        return suma;
    }

    // Actualiza un pago existente.
    public Pago actualizar(Integer id, Pago datos) {
        Pago pago = obtener(id);
        pago.setPedido(datos.getPedido());
        pago.setMonto(datos.getMonto());
        pago.setMetodo(datos.getMetodo());
        pago.setProveedorBilletera(datos.getProveedorBilletera());
        pago.setReferencia(datos.getReferencia());
        pago.setEstado(datos.getEstado());
        pago.setAprobadoPor(datos.getAprobadoPor());
        if (datos.getFechaPago() != null) {
            pago.setFechaPago(datos.getFechaPago());
        }
        return repository.save(pago);
    }

    // Borra un pago por su id.
    public void eliminar(Integer id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Pago no encontrado: " + id);
        }
        repository.deleteById(id);
    }
}
