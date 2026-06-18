package com.novatech.store.service;

import com.novatech.store.entity.DetallePedido;
import com.novatech.store.exception.ResourceNotFoundException;
import com.novatech.store.repository.DetallePedidoRepository;
import java.util.List;
import org.springframework.stereotype.Service;

// Logica de negocio para los renglones del pedido.
@Service
public class DetallePedidoService {

    private final DetallePedidoRepository repository;

    public DetallePedidoService(DetallePedidoRepository repository) {
        this.repository = repository;
    }

    // Trae todos los renglones de pedido.
    public List<DetallePedido> listar() {
        return repository.findAll();
    }

    // Trae un renglon por su id. Si no existe, lanza error 404.
    public DetallePedido obtener(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Detalle de pedido no encontrado: " + id));
    }

    // Crea un renglon nuevo (id en null para que lo genere la base).
    public DetallePedido crear(DetallePedido detalle) {
        detalle.setIdDetalle(null);
        return repository.save(detalle);
    }

    // Actualiza un renglon existente.
    public DetallePedido actualizar(Integer id, DetallePedido datos) {
        DetallePedido detalle = obtener(id);
        detalle.setPedido(datos.getPedido());
        detalle.setProducto(datos.getProducto());
        detalle.setCantidad(datos.getCantidad());
        detalle.setPrecioUnitario(datos.getPrecioUnitario());
        return repository.save(detalle);
    }

    // Borra un renglon por su id.
    public void eliminar(Integer id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Detalle de pedido no encontrado: " + id);
        }
        repository.deleteById(id);
    }
}
