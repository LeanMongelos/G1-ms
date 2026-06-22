package com.novatech.store.service;

import com.novatech.store.entity.InteraccionCrm;
import com.novatech.store.entity.PerfilCliente;
import com.novatech.store.exception.ResourceNotFoundException;
import com.novatech.store.repository.InteraccionCrmRepository;
import com.novatech.store.repository.PerfilClienteRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class InteraccionCrmService {

    private final InteraccionCrmRepository repository;
    private final PerfilClienteRepository perfilRepository;

    public InteraccionCrmService(InteraccionCrmRepository repository,
                                 PerfilClienteRepository perfilRepository) {
        this.repository = repository;
        this.perfilRepository = perfilRepository;
    }

    public List<InteraccionCrm> listar() {
        return repository.findAll();
    }

    public InteraccionCrm obtener(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Interacción no encontrada: " + id));
    }

    public List<InteraccionCrm> listarPorCliente(Integer idCliente) {
        return repository.findByClienteIdCliente(idCliente);
    }

    public InteraccionCrm crear(InteraccionCrm interaccion) {
        interaccion.setIdInteraccion(null);
        if (interaccion.getFecha() == null) {
            interaccion.setFecha(LocalDateTime.now());
        }
        if (interaccion.getEstado() == null || interaccion.getEstado().isBlank()) {
            interaccion.setEstado("ABIERTA");
        }
        if (interaccion.getPrioridad() == null || interaccion.getPrioridad().isBlank()) {
            interaccion.setPrioridad("MEDIA");
        }
        vincularCliente(interaccion);
        return repository.save(interaccion);
    }

    public InteraccionCrm actualizar(Integer id, InteraccionCrm datos) {
        InteraccionCrm interaccion = obtener(id);
        interaccion.setTipo(datos.getTipo());
        interaccion.setTitulo(datos.getTitulo());
        interaccion.setDescripcion(datos.getDescripcion());
        interaccion.setPrioridad(datos.getPrioridad());
        interaccion.setEstado(datos.getEstado());
        if (datos.getCliente() != null) {
            vincularCliente(datos);
            interaccion.setCliente(datos.getCliente());
        }
        return repository.save(interaccion);
    }

    public void eliminar(Integer id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Interacción no encontrada: " + id);
        }
        repository.deleteById(id);
    }

    private void vincularCliente(InteraccionCrm interaccion) {
        if (interaccion.getCliente() != null && interaccion.getCliente().getIdCliente() != null) {
            PerfilCliente perfil = perfilRepository.findById(interaccion.getCliente().getIdCliente())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Cliente no encontrado: " + interaccion.getCliente().getIdCliente()));
            interaccion.setCliente(perfil);
        }
    }
}
