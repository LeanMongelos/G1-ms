package com.novatech.store.repository;

import com.novatech.store.entity.Conversacion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repositorio JPA `ConversacionRepository`: consultas y persistencia de entidad Conversacion en MySQL.
 */
public interface ConversacionRepository extends JpaRepository<Conversacion, Integer> {

    List<Conversacion> findByCanalIgnoreCase(String canal);

    List<Conversacion> findByEstadoIgnoreCase(String estado);

    List<Conversacion> findByCanalIgnoreCaseAndEstadoIgnoreCase(String canal, String estado);

    @Query("""
            SELECT c FROM Conversacion c
            WHERE (c.cliente IS NOT NULL AND c.cliente.usuario.idUsuario = :idUsuario)
               OR (LOWER(c.contactoEmail) = LOWER(:email))
            ORDER BY c.ultimaActividad DESC, c.idConversacion DESC
            """)
    List<Conversacion> findByClienteUsuarioOrEmail(
            @Param("idUsuario") Integer idUsuario,
            @Param("email") String email);
}
