package com.novatech.store.repository;

import com.novatech.store.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

// Repositorio de Categoria. Solo con extender JpaRepository ya podemos
// guardar, listar, buscar por id y borrar categorias sin escribir nada mas.
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
}
