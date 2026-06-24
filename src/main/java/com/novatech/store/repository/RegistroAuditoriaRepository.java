package com.novatech.store.repository;

import com.novatech.store.entity.RegistroAuditoria;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repositorio JPA `RegistroAuditoriaRepository`: consultas y persistencia de entidad RegistroAuditoria en MySQL.
 */
public interface RegistroAuditoriaRepository extends JpaRepository<RegistroAuditoria, Integer> {

    List<RegistroAuditoria> findTop50ByOrderByFechaDesc();

    @Query("""
            SELECT r FROM RegistroAuditoria r
            WHERE (:q IS NULL OR LOWER(r.detalle) LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(r.usuarioNombre) LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(r.entidad) LIKE LOWER(CONCAT('%', :q, '%')))
            AND (:entidad IS NULL OR r.entidad = :entidad)
            AND (:usuario IS NULL OR r.usuarioNombre = :usuario)
            ORDER BY r.fecha DESC
            """)
    List<RegistroAuditoria> buscar(
            @Param("q") String q,
            @Param("entidad") String entidad,
            @Param("usuario") String usuario);
}
