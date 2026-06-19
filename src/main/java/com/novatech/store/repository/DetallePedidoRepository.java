package com.novatech.store.repository;

import com.novatech.store.entity.DetallePedido;
import org.springframework.data.jpa.repository.JpaRepository;

// Repositorio para los renglones del pedido.
public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Integer> {
}
