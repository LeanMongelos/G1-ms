package com.novatech.store.repository;

import com.novatech.store.entity.MensajeCliente;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio JPA `MensajeClienteRepository`: consultas y persistencia de entidad MensajeCliente en MySQL.
 */
public interface MensajeClienteRepository extends JpaRepository<MensajeCliente, Integer> {

    List<MensajeCliente> findByCampanaIdCampana(Integer idCampana);
}
