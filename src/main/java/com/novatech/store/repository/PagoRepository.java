package com.novatech.store.repository;

import com.novatech.store.entity.Pago;
import org.springframework.data.jpa.repository.JpaRepository;

// Repositorio para los pagos.
public interface PagoRepository extends JpaRepository<Pago, Integer> {
}
