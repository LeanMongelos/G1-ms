package com.novatech.store.repository;

import com.novatech.store.entity.RolPermiso;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio JPA `RolPermisoRepository`: consultas y persistencia de entidad RolPermiso en MySQL.
 */
public interface RolPermisoRepository extends JpaRepository<RolPermiso, Integer> {

    List<RolPermiso> findByRolClave(String rolClave);

    void deleteByRolClave(String rolClave);
}
