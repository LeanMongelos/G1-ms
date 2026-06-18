package com.novatech.store.repository;

import com.novatech.store.entity.PerfilCliente;
import org.springframework.data.jpa.repository.JpaRepository;

// Repositorio para los perfiles de cliente.
public interface PerfilClienteRepository extends JpaRepository<PerfilCliente, Integer> {
}
