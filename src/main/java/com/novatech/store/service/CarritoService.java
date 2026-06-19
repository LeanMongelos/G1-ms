package com.novatech.store.service;

import com.novatech.store.entity.Carrito;
import com.novatech.store.exception.ResourceNotFoundException;
import com.novatech.store.repository.CarritoRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

// Logica de negocio para los carritos.
@Service
public class CarritoService {

    private final CarritoRepository repository;

    public CarritoService(CarritoRepository repository) {
        this.repository = repository;
    }

    // Trae todos los carritos.
    public List<Carrito> listar() {
        return repository.findAll();
    }

    // Trae un carrito por su id. Si no existe, lanza error 404.
    public Carrito obtener(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado: " + id));
    }

    // Crea un carrito nuevo. Si no nos mandan la fecha, ponemos la de ahora.
    public Carrito crear(Carrito carrito) {
        carrito.setIdCarrito(null);
        if (carrito.getFechaCreacion() == null) {
            carrito.setFechaCreacion(LocalDateTime.now());
        }
        return repository.save(carrito);
    }

    // Actualiza un carrito existente.
    public Carrito actualizar(Integer id, Carrito datos) {
        Carrito carrito = obtener(id);
        carrito.setUsuario(datos.getUsuario());
        if (datos.getFechaCreacion() != null) {
            carrito.setFechaCreacion(datos.getFechaCreacion());
        }
        return repository.save(carrito);
    }

    // Borra un carrito por su id.
    public void eliminar(Integer id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Carrito no encontrado: " + id);
        }
        repository.deleteById(id);
    }
}
