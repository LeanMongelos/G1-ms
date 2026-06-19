package com.novatech.store.repository;

import com.novatech.store.entity.PlanCuotas;
import org.springframework.data.jpa.repository.JpaRepository;

// Repositorio para los planes de cuotas.
public interface PlanCuotasRepository extends JpaRepository<PlanCuotas, Integer> {
}
