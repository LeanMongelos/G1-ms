package com.novatech.store.repository;

import com.novatech.store.entity.ListaPrecio;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio JPA `ListaPrecioRepository`: consultas y persistencia de entidad ListaPrecio en MySQL.
 */
public interface ListaPrecioRepository extends JpaRepository<ListaPrecio, Integer> {

    Optional<ListaPrecio> findByCodigoIgnoreCase(String codigo);

    List<ListaPrecio> findAllByOrderByCodigoAsc();
}
