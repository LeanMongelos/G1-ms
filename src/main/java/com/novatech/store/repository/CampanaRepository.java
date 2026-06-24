package com.novatech.store.repository;

import com.novatech.store.entity.Campana;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio JPA `CampanaRepository`: consultas y persistencia de entidad Campana en MySQL.
 */
public interface CampanaRepository extends JpaRepository<Campana, Integer> {
}
