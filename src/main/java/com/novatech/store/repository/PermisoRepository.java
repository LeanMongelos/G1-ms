package com.novatech.store.repository;

import com.novatech.store.entity.Permiso;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio JPA `PermisoRepository`: consultas y persistencia de entidad Permiso en MySQL.
 */
public interface PermisoRepository extends JpaRepository<Permiso, Integer> {

    List<Permiso> findAllByOrderByModuloAscClaveAsc();

    Optional<Permiso> findByClaveIgnoreCase(String clave);
}
