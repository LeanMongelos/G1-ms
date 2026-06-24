package com.novatech.store.service;

import com.novatech.store.entity.Promocion;
import com.novatech.store.exception.ResourceNotFoundException;
import com.novatech.store.repository.PromocionRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Servicio `PromocionService`: reglas de negocio, transacciones y orquestación de Promocion. Los controllers delegan aquí; no accede HTTP directamente.
 */
@Service
public class PromocionService {

    private final PromocionRepository repository;

    public PromocionService(PromocionRepository repository) {
        this.repository = repository;
    }

    public List<Promocion> listar() {
        return repository.findAll();
    }

    public Promocion obtener(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promoción no encontrada: " + id));
    }

    public Promocion crear(Promocion promocion) {
        promocion.setIdPromocion(null);
        if (promocion.getEstado() == null || promocion.getEstado().isBlank()) {
            promocion.setEstado("BORRADOR");
        }
        if (promocion.getSegmentoObjetivo() == null || promocion.getSegmentoObjetivo().isBlank()) {
            promocion.setSegmentoObjetivo("TODOS");
        }
        return repository.save(promocion);
    }

    public Promocion actualizar(Integer id, Promocion datos) {
        Promocion promocion = obtener(id);
        promocion.setTitulo(datos.getTitulo());
        promocion.setDescripcion(datos.getDescripcion());
        promocion.setPorcentajeDescuento(datos.getPorcentajeDescuento());
        promocion.setCodigo(datos.getCodigo());
        promocion.setFechaInicio(datos.getFechaInicio());
        promocion.setFechaFin(datos.getFechaFin());
        promocion.setEstado(datos.getEstado());
        promocion.setSegmentoObjetivo(datos.getSegmentoObjetivo());
        return repository.save(promocion);
    }

    public Promocion activar(Integer id) {
        Promocion promocion = obtener(id);
        promocion.setEstado("ACTIVA");
        if (promocion.getFechaInicio() == null) {
            promocion.setFechaInicio(LocalDateTime.now());
        }
        return repository.save(promocion);
    }

    public void eliminar(Integer id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Promoción no encontrada: " + id);
        }
        repository.deleteById(id);
    }
}
