package com.novatech.store.repository;

import com.novatech.store.entity.Promocion;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio JPA `PromocionRepository`: consultas y persistencia de entidad Promocion en MySQL.
 */
public interface PromocionRepository extends JpaRepository<Promocion, Integer> {
}
