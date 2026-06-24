package com.novatech.store.service;

import com.novatech.store.entity.ConfiguracionSistema;
import com.novatech.store.exception.ResourceNotFoundException;
import com.novatech.store.repository.ConfiguracionSistemaRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

/**
 * Servicio `ConfiguracionService`: reglas de negocio, transacciones y orquestación de Configuracion. Los controllers delegan aquí; no accede HTTP directamente.
 */
@Service
public class ConfiguracionService {

    private final ConfiguracionSistemaRepository repository;

    public ConfiguracionService(ConfiguracionSistemaRepository repository) {
        this.repository = repository;
    }

    public List<ConfiguracionSistema> listarPorGrupo(String grupo) {
        return repository.findByGrupoIgnoreCase(grupo);
    }

    public Map<String, String> mapaPorGrupo(String grupo) {
        Map<String, String> mapa = new HashMap<>();
        for (ConfiguracionSistema c : listarPorGrupo(grupo)) {
            mapa.put(c.getClave(), c.getValor());
        }
        return mapa;
    }

    public ConfiguracionSistema guardar(ConfiguracionSistema config) {
        if (config.getClave() != null) {
            return repository.findByClaveIgnoreCase(config.getClave())
                    .map(existente -> {
                        existente.setValor(config.getValor());
                        if (config.getDescripcion() != null) {
                            existente.setDescripcion(config.getDescripcion());
                        }
                        return repository.save(existente);
                    })
                    .orElseGet(() -> repository.save(config));
        }
        return repository.save(config);
    }

    public List<ConfiguracionSistema> guardarGrupo(String grupo, Map<String, String> valores) {
        return valores.entrySet().stream()
                .map(e -> {
                    ConfiguracionSistema c = repository.findByClaveIgnoreCase(e.getKey())
                            .orElseGet(() -> {
                                ConfiguracionSistema nueva = new ConfiguracionSistema();
                                nueva.setGrupo(grupo);
                                nueva.setClave(e.getKey());
                                return nueva;
                            });
                    c.setValor(e.getValue());
                    return repository.save(c);
                })
                .toList();
    }

    public ConfiguracionSistema obtenerPorClave(String clave) {
        return repository.findByClaveIgnoreCase(clave)
                .orElseThrow(() -> new ResourceNotFoundException("Configuración no encontrada: " + clave));
    }
}
