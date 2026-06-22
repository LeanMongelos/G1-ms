package com.novatech.store.repository;

import com.novatech.store.entity.DetallePedido;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Integer> {

    List<DetallePedido> findByPedidoIdPedido(Integer idPedido);
}
