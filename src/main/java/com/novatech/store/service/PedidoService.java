package com.novatech.store.service;

import com.novatech.store.entity.Pedido;
import com.novatech.store.exception.ResourceNotFoundException;
import com.novatech.store.repository.PedidoRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

// Logica de negocio para los pedidos.
@Service
public class PedidoService {

    private final PedidoRepository repository;

    public PedidoService(PedidoRepository repository) {
        this.repository = repository;
    }

    // Trae todos los pedidos.
    public List<Pedido> listar() {
        return repository.findAll();
    }

    // Trae un pedido por su id. Si no existe, lanza error 404.
    public Pedido obtener(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado: " + id));
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
