package com.novatech.store.repository;

import com.novatech.store.entity.PerfilCliente;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repositorio JPA `PerfilClienteRepository`: consultas y persistencia de entidad PerfilCliente en MySQL.
 */
public interface PerfilClienteRepository extends JpaRepository<PerfilCliente, Integer> {

    Optional<PerfilCliente> findByUsuario_IdUsuario(Integer idUsuario);

    List<PerfilCliente> findByActivoTrueOrderByIdClienteDesc();

    @Query("""
            SELECT p FROM PerfilCliente p
            WHERE (p.activo = true OR p.activo IS NULL)
            AND (:tipo IS NULL OR LOWER(p.tipoCliente) = LOWER(:tipo))
            AND (:q IS NULL OR LOWER(p.usuario.nombre) LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(p.usuario.email) LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(p.telefono) LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(p.ciudad) LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(p.contacto) LIKE LOWER(CONCAT('%', :q, '%')))
            ORDER BY p.idCliente DESC
            """)
    List<PerfilCliente> buscarActivos(@Param("q") String q, @Param("tipo") String tipo);
}
