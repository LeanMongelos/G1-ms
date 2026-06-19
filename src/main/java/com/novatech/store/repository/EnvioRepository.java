package com.novatech.store.repository;

import com.novatech.store.entity.Envio;
import org.springframework.data.jpa.repository.JpaRepository;

// Repositorio para los envios.
public interface EnvioRepository extends JpaRepository<Envio, Integer> {
}
