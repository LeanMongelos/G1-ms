package com.novatech.store.service;

import com.novatech.store.entity.CatalogoMaestro;
import com.novatech.store.repository.CatalogoMaestroRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Servicio `CatalogoConfigService`: reglas de negocio, transacciones y orquestación de CatalogoConfig. Los controllers delegan aquí; no accede HTTP directamente.
 */
@Service
public class CatalogoConfigService {

    private final CatalogoMaestroRepository repository;

    public CatalogoConfigService(CatalogoMaestroRepository repository) {
        this.repository = repository;
    }

    public Map<String, List<CatalogoMaestro>> listarTodo() {
        Map<String, List<CatalogoMaestro>> out = new HashMap<>();
        out.put("categorias", repository.findByTipoOrderByOrdenAscNombreAsc("CATEGORIA"));
        out.put("depositos", repository.findByTipoOrderByOrdenAscNombreAsc("DEPOSITO"));
        out.put("condicionesPago", repository.findByTipoOrderByOrdenAscNombreAsc("CONDICION_PAGO"));
        return out;
    }

    @Transactional
    public CatalogoMaestro crear(String tipo, CatalogoMaestro item) {
        item.setTipo(tipo);
        if (item.getActivo() == null) {
            item.setActivo(true);
        }
        if (item.getOrden() == null) {
            item.setOrden(0);
        }
        return repository.save(item);
    }

    @Transactional
    public CatalogoMaestro actualizar(Integer id, CatalogoMaestro datos) {
        CatalogoMaestro existente = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Catálogo no encontrado"));
        if (datos.getCodigo() != null) {
            existente.setCodigo(datos.getCodigo());
        }
        if (datos.getNombre() != null) {
            existente.setNombre(datos.getNombre());
        }
        if (datos.getDatosJson() != null) {
            existente.setDatosJson(datos.getDatosJson());
        }
        if (datos.getActivo() != null) {
            existente.setActivo(datos.getActivo());
        }
        if (datos.getOrden() != null) {
            existente.setOrden(datos.getOrden());
        }
        return repository.save(existente);
    }
}
