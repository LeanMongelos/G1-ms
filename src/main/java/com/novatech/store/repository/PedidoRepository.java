package com.novatech.store.repository;

import com.novatech.store.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

// Repositorio para los pedidos.
public interface PedidoRepository extends JpaRepository<Pedido, Integer> {
}
