package com.novatech.store.repository;

import com.novatech.store.entity.LogSistema;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repositorio JPA `LogSistemaRepository`: consultas y persistencia de entidad LogSistema en MySQL.
 */
public interface LogSistemaRepository extends JpaRepository<LogSistema, Integer> {

    List<LogSistema> findTop50ByOrderByFechaDesc();

    @Query("SELECT l FROM LogSistema l WHERE l.fecha >= :desde ORDER BY l.fecha DESC")
    List<LogSistema> findDesde(@Param("desde") LocalDateTime desde);

    void deleteByFechaBefore(LocalDateTime fecha);
}
