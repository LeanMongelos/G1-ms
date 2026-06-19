package com.novatech.store.repository;

import com.novatech.store.entity.DetalleCarrito;
import org.springframework.data.jpa.repository.JpaRepository;

// Repositorio para los renglones del carrito.
public interface DetalleCarritoRepository extends JpaRepository<DetalleCarrito, Integer> {
}
