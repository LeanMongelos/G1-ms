package com.novatech.store.repository;

import com.novatech.store.entity.Factura;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacturaRepository extends JpaRepository<Factura, Integer> {

    List<Factura> findByPedidoIdPedido(Integer idPedido);

    List<Factura> findByPresupuestoIdPresupuesto(Integer idPresupuesto);
}
