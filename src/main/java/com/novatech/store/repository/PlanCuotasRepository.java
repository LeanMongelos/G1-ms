package com.novatech.store.repository;

import com.novatech.store.entity.PlanCuotas;
import com.novatech.store.entity.PlanCuotas;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

// Repositorio para los planes de cuotas.
/**
 * Repositorio JPA `PlanCuotasRepository`: consultas y persistencia de entidad PlanCuotas en MySQL.
 */
public interface PlanCuotasRepository extends JpaRepository<PlanCuotas, Integer> {

    List<PlanCuotas> findByPedidoIdPedido(Integer idPedido);
}
