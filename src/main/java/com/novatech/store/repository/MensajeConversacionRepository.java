package com.novatech.store.repository;

import com.novatech.store.entity.MensajeConversacion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio JPA `MensajeConversacionRepository`: consultas y persistencia de entidad MensajeConversacion en MySQL.
 */
public interface MensajeConversacionRepository extends JpaRepository<MensajeConversacion, Integer> {

    List<MensajeConversacion> findByConversacionIdConversacionOrderByFechaAsc(Integer idConversacion);
}
