package com.novatech.store.service;

import com.novatech.store.dto.IntegracionCanalResponse;
import com.novatech.store.entity.IntegracionCanal;
import com.novatech.store.exception.ResourceNotFoundException;
import com.novatech.store.repository.IntegracionCanalRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class IntegracionCanalService {

    private final IntegracionCanalRepository repository;

    public IntegracionCanalService(IntegracionCanalRepository repository) {
        this.repository = repository;
    }

    public List<IntegracionCanal> listar() {
        return repository.findAll();
    }

    public List<IntegracionCanalResponse> listarEnmascaradas() {
        return listar().stream().map(this::aResponse).toList();
    }

    public IntegracionCanalResponse actualizarEnmascarada(Integer id, IntegracionCanal datos) {
        return aResponse(actualizar(id, datos));
    }

    private IntegracionCanalResponse aResponse(IntegracionCanal i) {
        return IntegracionCanalResponse.enmascarada(
                i.getIdIntegracion(), i.getTipo(), i.getNombre(),
                i.getActivo(), i.getEstadoConexion(), i.getConfigJson());
    }

    public IntegracionCanal obtenerPorTipo(String tipo) {
        return repository.findByTipoIgnoreCase(tipo)
                .orElseThrow(() -> new ResourceNotFoundException("Integración no encontrada: " + tipo));
    }

    public IntegracionCanal actualizar(Integer id, IntegracionCanal datos) {
        IntegracionCanal actual = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Integración no encontrada: " + id));
        if (datos.getNombre() != null) {
            actual.setNombre(datos.getNombre());
        }
        if (datos.getActivo() != null) {
            actual.setActivo(datos.getActivo());
        }
        if (datos.getEstadoConexion() != null) {
            actual.setEstadoConexion(datos.getEstadoConexion());
        }
        if (datos.getConfigJson() != null) {
            actual.setConfigJson(datos.getConfigJson());
        }
        return repository.save(actual);
    }
}
