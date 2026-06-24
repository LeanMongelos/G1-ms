package com.novatech.store.service;

import com.novatech.store.entity.DetalleCarrito;
import com.novatech.store.exception.ResourceNotFoundException;
import com.novatech.store.repository.DetalleCarritoRepository;
import java.util.List;
import org.springframework.stereotype.Service;

// Logica de negocio para los renglones del carrito.
/**
 * Servicio `DetalleCarritoService`: reglas de negocio, transacciones y orquestación de DetalleCarrito. Los controllers delegan aquí; no accede HTTP directamente.
 */
@Service
public class DetalleCarritoService {

    private final DetalleCarritoRepository repository;

    public DetalleCarritoService(DetalleCarritoRepository repository) {
        this.repository = repository;
    }

    // Trae todos los renglones de carrito.
    public List<DetalleCarrito> listar() {
        return repository.findAll();
    }

    // Trae un renglon por su id. Si no existe, lanza error 404.
    public DetalleCarrito obtener(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Detalle de carrito no encontrado: " + id));
    }

    // Crea un renglon nuevo (id en null para que lo genere la base).
    public DetalleCarrito crear(DetalleCarrito detalle) {
        detalle.setIdDetalleCarrito(null);
        return repository.save(detalle);
    }

    // Actualiza un renglon existente.
    public DetalleCarrito actualizar(Integer id, DetalleCarrito datos) {
        DetalleCarrito detalle = obtener(id);
        detalle.setCarrito(datos.getCarrito());
        detalle.setProducto(datos.getProducto());
        detalle.setCantidad(datos.getCantidad());
        return repository.save(detalle);
    }

    // Borra un renglon por su id.
    public void eliminar(Integer id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Detalle de carrito no encontrado: " + id);
        }
        repository.deleteById(id);
    }
}
