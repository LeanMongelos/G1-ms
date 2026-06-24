package com.novatech.store.repository;

import com.novatech.store.entity.Factura;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repositorio JPA `FacturaRepository`: consultas y persistencia de entidad Factura en MySQL.
 */
public interface FacturaRepository extends JpaRepository<Factura, Integer> {

    List<Factura> findAllByOrderByFechaEmisionDescIdFacturaDesc();

    List<Factura> findByPedidoIdPedido(Integer idPedido);

    List<Factura> findByPresupuestoIdPresupuesto(Integer idPresupuesto);

    @Query("""
            SELECT f FROM Factura f
            WHERE f.pedido IS NOT NULL AND f.pedido.usuario.idUsuario = :idUsuario
            ORDER BY f.fechaEmision DESC, f.idFactura DESC
            """)
    List<Factura> findByPedidoUsuarioOrderByFechaEmisionDesc(@Param("idUsuario") Integer idUsuario);
}
