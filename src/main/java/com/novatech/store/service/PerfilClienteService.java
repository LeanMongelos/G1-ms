package com.novatech.store.service;

import com.novatech.store.entity.PerfilCliente;
import com.novatech.store.exception.ResourceNotFoundException;
import com.novatech.store.repository.PerfilClienteRepository;
import java.util.List;
import org.springframework.stereotype.Service;

// Logica de negocio para los perfiles de cliente.
@Service
public class PerfilClienteService {

    private final PerfilClienteRepository repository;

    public PerfilClienteService(PerfilClienteRepository repository) {
        this.repository = repository;
    }

    // Trae todos los perfiles de cliente.
    public List<PerfilCliente> listar() {
        return repository.findAll();
    }

    // Trae un perfil por su id. Si no existe, lanza error 404.
    public PerfilCliente obtener(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil de cliente no encontrado: " + id));
    }

    // Crea un perfil nuevo (id en null para que lo genere la base).
    public PerfilCliente crear(PerfilCliente perfil) {
        perfil.setIdCliente(null);
        return repository.save(perfil);
    }

    // Actualiza un perfil existente: lo busca, le cambia los datos y lo guarda.
    public PerfilCliente actualizar(Integer id, PerfilCliente datos) {
        PerfilCliente perfil = obtener(id);
        perfil.setUsuario(datos.getUsuario());
        perfil.setDireccion(datos.getDireccion());
        perfil.setTelefono(datos.getTelefono());
        perfil.setHistorialCrediticio(datos.getHistorialCrediticio());
        perfil.setTipoCliente(datos.getTipoCliente());
        return repository.save(perfil);
    }

    // Borra un perfil por su id.
    public void eliminar(Integer id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Perfil de cliente no encontrado: " + id);
        }
        repository.deleteById(id);
    }
}
