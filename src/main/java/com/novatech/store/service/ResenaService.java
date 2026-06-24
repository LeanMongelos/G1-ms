package com.novatech.store.service;

import com.novatech.store.entity.Resena;
import com.novatech.store.exception.ResourceNotFoundException;
import com.novatech.store.repository.ResenaRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

// Logica de negocio para las resenas.
/**
 * Servicio `ResenaService`: reglas de negocio, transacciones y orquestación de Resena. Los controllers delegan aquí; no accede HTTP directamente.
 */
@Service
public class ResenaService {

    private final ResenaRepository repository;

    public ResenaService(ResenaRepository repository) {
        this.repository = repository;
    }

    // Trae todas las resenas.
    public List<Resena> listar() {
        return repository.findAll();
    }

    // Trae todas las resenas de un producto.
    public List<Resena> porProducto(Integer idProducto) {
        return repository.findByProductoIdProducto(idProducto);
    }

    // Trae una resena por su id. Si no existe, lanza error 404.
    public Resena obtener(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resena no encontrada: " + id));
    }

    // Crea una resena nueva. Si no viene la fecha, ponemos la de ahora.
    public Resena crear(Resena resena) {
        resena.setIdResena(null);
        if (resena.getFecha() == null) {
            resena.setFecha(LocalDateTime.now());
        }
        return repository.save(resena);
    }

    // Actualiza una resena existente.
    public Resena actualizar(Integer id, Resena datos) {
        Resena resena = obtener(id);
        resena.setProducto(datos.getProducto());
        resena.setUsuario(datos.getUsuario());
        resena.setComentario(datos.getComentario());
        resena.setPuntuacion(datos.getPuntuacion());
        if (datos.getFecha() != null) {
            resena.setFecha(datos.getFecha());
        }
        return repository.save(resena);
    }

    // Borra una resena por su id.
    public void eliminar(Integer id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Resena no encontrada: " + id);
        }
        repository.deleteById(id);
    }
}
