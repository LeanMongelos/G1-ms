package com.novatech.store.repository;

import com.novatech.store.entity.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;

// Repositorio para los carritos.
/**
 * Repositorio JPA `CarritoRepository`: consultas y persistencia de entidad Carrito en MySQL.
 */
public interface CarritoRepository extends JpaRepository<Carrito, Integer> {
}
