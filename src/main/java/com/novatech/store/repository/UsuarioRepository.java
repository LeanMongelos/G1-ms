package com.novatech.store.repository;

import com.novatech.store.entity.Usuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio JPA `UsuarioRepository`: consultas y persistencia de entidad Usuario en MySQL.
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByEmail(String email);

    long countByRolIgnoreCase(String rol);
}
