package com.novatech.store.repository;

import com.novatech.store.entity.IntegracionCanal;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IntegracionCanalRepository extends JpaRepository<IntegracionCanal, Integer> {

    Optional<IntegracionCanal> findByTipoIgnoreCase(String tipo);
}
