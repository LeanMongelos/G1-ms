package com.novatech.store.repository;

import com.novatech.store.entity.InteraccionCrm;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio JPA `InteraccionCrmRepository`: consultas y persistencia de entidad InteraccionCrm en MySQL.
 */
public interface InteraccionCrmRepository extends JpaRepository<InteraccionCrm, Integer> {

    List<InteraccionCrm> findByClienteIdCliente(Integer idCliente);

    List<InteraccionCrm> findByEstado(String estado);
}
