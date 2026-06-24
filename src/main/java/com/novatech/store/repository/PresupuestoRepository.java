package com.novatech.store.repository;

import com.novatech.store.entity.Presupuesto;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio JPA `PresupuestoRepository`: consultas y persistencia de entidad Presupuesto en MySQL.
 */
public interface PresupuestoRepository extends JpaRepository<Presupuesto, Integer> {
}
