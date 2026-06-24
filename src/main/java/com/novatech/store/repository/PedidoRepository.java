package com.novatech.store.repository;

import com.novatech.store.entity.Pedido;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio JPA `PedidoRepository`: consultas y persistencia de entidad Pedido en MySQL.
 */
public interface PedidoRepository extends JpaRepository<Pedido, Integer> {

    List<Pedido> findByUsuario_IdUsuarioOrderByFechaDesc(Integer idUsuario);
}
