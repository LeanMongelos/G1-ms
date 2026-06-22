package com.novatech.store.repository;

import com.novatech.store.entity.Presupuesto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PresupuestoRepository extends JpaRepository<Presupuesto, Integer> {
}
