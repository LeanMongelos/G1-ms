package com.novatech.store.service;

import com.novatech.store.entity.Envio;
import com.novatech.store.exception.ResourceNotFoundException;
import com.novatech.store.repository.EnvioRepository;
import java.util.List;
import org.springframework.stereotype.Service;

// Logica de negocio para los envios.
@Service
public class EnvioService {

    private final EnvioRepository repository;

    public EnvioService(EnvioRepository repository) {
        this.repository = repository;
    }

    // Trae todos los envios.
    public List<Envio> listar() {
        return repository.findAll();
    }

    // Trae un envio por su id. Si no existe, lanza error 404.
    public Envio obtener(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Envio no encontrado: " + id));
    }

    // Crea un envio nuevo. Si no viene estado, lo dejamos "PREPARANDO".
    public Envio crear(Envio envio) {
        envio.setIdEnvio(null);
        if (envio.getEstadoEnvio() == null || envio.getEstadoEnvio().isBlank()) {
            envio.setEstadoEnvio("PREPARANDO");
        }
        return repository.save(envio);
    }

    // Actualiza un envio existente.
    public Envio actualizar(Integer id, Envio datos) {
        Envio envio = obtener(id);
        envio.setPedido(datos.getPedido());
        envio.setDireccionEnvio(datos.getDireccionEnvio());
        envio.setEmpresaLogistica(datos.getEmpresaLogistica());
        envio.setEstadoEnvio(datos.getEstadoEnvio());
        return repository.save(envio);
    }

    // Borra un envio por su id.
    public void eliminar(Integer id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Envio no encontrado: " + id);
        }
        repository.deleteById(id);
    }
}
