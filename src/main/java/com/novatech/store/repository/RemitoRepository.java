package com.novatech.store.repository;

import com.novatech.store.entity.Remito;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio JPA `RemitoRepository`: consultas y persistencia de entidad Remito en MySQL.
 */
public interface RemitoRepository extends JpaRepository<Remito, Integer> {
}
