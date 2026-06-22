package com.novatech.store.repository;

import com.novatech.store.entity.AlicuotaIva;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlicuotaIvaRepository extends JpaRepository<AlicuotaIva, Integer> {

    List<AlicuotaIva> findAllByOrderByPorcentajeAsc();
}
