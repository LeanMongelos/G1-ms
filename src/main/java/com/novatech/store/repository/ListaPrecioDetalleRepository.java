package com.novatech.store.repository;

import com.novatech.store.entity.ListaPrecioDetalle;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio JPA `ListaPrecioDetalleRepository`: consultas y persistencia de entidad ListaPrecioDetalle en MySQL.
 */
public interface ListaPrecioDetalleRepository extends JpaRepository<ListaPrecioDetalle, Integer> {

    List<ListaPrecioDetalle> findByListaPrecioIdListaPrecioOrderByProductoNombreAsc(Integer idLista);

    Optional<ListaPrecioDetalle> findByListaPrecioIdListaPrecioAndProductoIdProducto(
            Integer idLista, Integer idProducto);
}
