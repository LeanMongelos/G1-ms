package com.novatech.store.repository;

import com.novatech.store.entity.RolRbac;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio JPA `RolRbacRepository`: consultas y persistencia de entidad RolRbac en MySQL.
 */
public interface RolRbacRepository extends JpaRepository<RolRbac, Integer> {

    List<RolRbac> findAllByOrderByClaveAsc();

    Optional<RolRbac> findByClaveIgnoreCase(String clave);

    boolean existsByClaveIgnoreCase(String clave);
}
