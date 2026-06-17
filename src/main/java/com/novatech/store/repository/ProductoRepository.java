package com.novatech.store.repository;

import com.novatech.store.entity.Producto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

// Repositorio de Producto. Ademas de los metodos basicos, agregamos dos busquedas.
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    // Buscar todos los productos de una categoria.
    // "CategoriaIdCategoria" significa: entra al objeto categoria del producto y compara su idCategoria.
    List<Producto> findByCategoriaIdCategoria(Integer idCategoria);

    // Buscar productos cuyo nombre contenga un texto, sin importar mayusculas o minusculas.
    List<Producto> findByNombreContainingIgnoreCase(String nombre);
}
