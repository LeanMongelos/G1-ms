package com.novatech.store.repository;

import com.novatech.store.entity.Cuota;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CuotaRepository extends JpaRepository<Cuota, Integer> {

    List<Cuota> findByPlanIdPlan(Integer idPlan);

    List<Cuota> findByEstado(String estado);

    List<Cuota> findByEstadoAndFechaVencimientoBefore(String estado, LocalDate fecha);
}
