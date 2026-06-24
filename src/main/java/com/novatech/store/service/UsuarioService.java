package com.novatech.store.service;

import com.novatech.store.entity.Usuario;
import com.novatech.store.exception.ResourceNotFoundException;
import com.novatech.store.repository.UsuarioRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

// Logica de negocio para los usuarios.
/**
 * Servicio `UsuarioService`: reglas de negocio, transacciones y orquestación de Usuario. Los controllers delegan aquí; no accede HTTP directamente.
 */
@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    // Herramienta para encriptar (hashear) contrasenas. Spring nos la pasa sola
    // porque la creamos como @Bean en PasswordConfig.
    private final BCryptPasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository repository, BCryptPasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    // Trae todos los usuarios.
    public List<Usuario> listar() {
        return repository.findAll();
    }

    // Trae un usuario por su id. Si no existe, lanza error 404.
    public Usuario obtener(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + id));
    }

    // Crea un usuario nuevo.
    public Usuario crear(Usuario usuario) {
        usuario.setIdUsuario(null);
        // Si no nos mandan la fecha de registro, le ponemos la fecha y hora de ahora.
        if (usuario.getFechaRegistro() == null) {
            usuario.setFechaRegistro(LocalDateTime.now());
        }
        // Encriptamos la contrasena ANTES de guardarla para que nunca quede en texto plano.
        if (usuario.getContrasena() != null && !usuario.getContrasena().isBlank()) {
            usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        }
        return repository.save(usuario);
    }

    // Actualiza un usuario existente.
    public Usuario actualizar(Integer id, Usuario datos) {
        Usuario usuario = obtener(id);
        usuario.setNombre(datos.getNombre());
        usuario.setEmail(datos.getEmail());
        usuario.setRol(datos.getRol());
        // Solo cambiamos la contrasena si nos mandaron una nueva (que no este vacia).
        // Y siempre la guardamos hasheada con BCrypt.
        if (datos.getContrasena() != null && !datos.getContrasena().isBlank()) {
            usuario.setContrasena(passwordEncoder.encode(datos.getContrasena()));
        }
        return repository.save(usuario);
    }

    // Actualiza perfil propio (sin cambiar rol).
    public Usuario actualizarPerfil(Integer id, com.novatech.store.dto.ActualizarPerfilRequest datos) {
        Usuario usuario = obtener(id);
        usuario.setNombre(datos.nombre());
        usuario.setEmail(datos.email());
        if (datos.contrasena() != null && !datos.contrasena().isBlank()) {
            usuario.setContrasena(passwordEncoder.encode(datos.contrasena()));
        }
        return repository.save(usuario);
    }

    // Borra un usuario por su id.
    public void eliminar(Integer id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Usuario no encontrado: " + id);
        }
        repository.deleteById(id);
    }
}
