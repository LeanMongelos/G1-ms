package com.novatech.store.repository;

import com.novatech.store.entity.CatalogoMaestro;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio JPA `CatalogoMaestroRepository`: consultas y persistencia de entidad CatalogoMaestro en MySQL.
 */
public interface CatalogoMaestroRepository extends JpaRepository<CatalogoMaestro, Integer> {

    List<CatalogoMaestro> findByTipoOrderByOrdenAscNombreAsc(String tipo);
}
