package com.novatech.store.repository;

import com.novatech.store.entity.OrdenCompra;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio JPA `OrdenCompraRepository`: consultas y persistencia de entidad OrdenCompra en MySQL.
 */
public interface OrdenCompraRepository extends JpaRepository<OrdenCompra, Integer> {
}
