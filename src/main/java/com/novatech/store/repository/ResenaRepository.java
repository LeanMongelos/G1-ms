package com.novatech.store.repository;

import com.novatech.store.entity.Resena;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

// Repositorio para las resenas.
public interface ResenaRepository extends JpaRepository<Resena, Integer> {

    // Buscar todas las resenas de un producto.
    List<Resena> findByProductoIdProducto(Integer idProducto);
}
