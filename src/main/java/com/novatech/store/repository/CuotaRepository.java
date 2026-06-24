package com.novatech.store.repository;

import com.novatech.store.entity.Cuota;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repositorio JPA `CuotaRepository`: consultas y persistencia de entidad Cuota en MySQL.
 */
public interface CuotaRepository extends JpaRepository<Cuota, Integer> {

    List<Cuota> findByPlanIdPlan(Integer idPlan);

    List<Cuota> findByEstado(String estado);

    List<Cuota> findByEstadoAndFechaVencimientoBefore(String estado, LocalDate fecha);

    @Query("""
            SELECT c FROM Cuota c
            WHERE c.plan.cliente.usuario.idUsuario = :idUsuario
               OR c.plan.pedido.usuario.idUsuario = :idUsuario
            ORDER BY c.fechaVencimiento ASC
            """)
    List<Cuota> findByUsuario(@Param("idUsuario") Integer idUsuario);
}
