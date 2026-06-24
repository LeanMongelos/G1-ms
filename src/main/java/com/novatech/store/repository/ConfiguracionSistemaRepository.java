package com.novatech.store.repository;

import com.novatech.store.entity.ConfiguracionSistema;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio JPA `ConfiguracionSistemaRepository`: consultas y persistencia de entidad ConfiguracionSistema en MySQL.
 */
public interface ConfiguracionSistemaRepository extends JpaRepository<ConfiguracionSistema, Integer> {

    List<ConfiguracionSistema> findByGrupoIgnoreCase(String grupo);

    Optional<ConfiguracionSistema> findByClaveIgnoreCase(String clave);
}
