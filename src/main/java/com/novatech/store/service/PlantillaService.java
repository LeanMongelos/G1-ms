package com.novatech.store.service;

import com.novatech.store.entity.PlantillaImpresion;
import com.novatech.store.repository.PlantillaImpresionRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PlantillaService {

    private final PlantillaImpresionRepository repository;

    public PlantillaService(PlantillaImpresionRepository repository) {
        this.repository = repository;
    }

    public List<PlantillaImpresion> listar() {
        return repository.findAllByOrderByTipoAscNombreAsc();
    }

    public PlantillaImpresion obtener(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plantilla no encontrada"));
    }

    @Transactional
    public PlantillaImpresion crear(PlantillaImpresion plantilla) {
        if (plantilla.getActivo() == null) {
            plantilla.setActivo(true);
        }
        if (Boolean.TRUE.equals(plantilla.getEsDefault())) {
            repository.findAll().stream()
                    .filter(p -> plantilla.getTipo().equals(p.getTipo()))
                    .forEach(p -> {
                        p.setEsDefault(false);
                        repository.save(p);
                    });
        }
        return repository.save(plantilla);
    }

    @Transactional
    public PlantillaImpresion actualizar(Integer id, PlantillaImpresion datos) {
        PlantillaImpresion existente = obtener(id);
        if (datos.getNombre() != null) {
            existente.setNombre(datos.getNombre());
        }
        if (datos.getContenidoJson() != null) {
            existente.setContenidoJson(datos.getContenidoJson());
        }
        if (datos.getActivo() != null) {
            existente.setActivo(datos.getActivo());
        }
        if (Boolean.TRUE.equals(datos.getEsDefault())) {
            repository.findAll().stream()
                    .filter(p -> existente.getTipo().equals(p.getTipo()))
                    .forEach(p -> {
                        p.setEsDefault(false);
                        repository.save(p);
                    });
            existente.setEsDefault(true);
        }
        return repository.save(existente);
    }

    @Transactional
    public PlantillaImpresion duplicar(Integer id) {
        PlantillaImpresion orig = obtener(id);
        PlantillaImpresion copia = new PlantillaImpresion();
        copia.setTipo(orig.getTipo());
        copia.setNombre(orig.getNombre() + " (copia)");
        copia.setContenidoJson(orig.getContenidoJson());
        copia.setEsDefault(false);
        copia.setActivo(true);
        return repository.save(copia);
    }
}
