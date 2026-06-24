package com.novatech.store.repository;

import com.novatech.store.entity.DetallePedido;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio JPA `DetallePedidoRepository`: consultas y persistencia de entidad DetallePedido en MySQL.
 */
public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Integer> {

    List<DetallePedido> findByPedidoIdPedido(Integer idPedido);
}
