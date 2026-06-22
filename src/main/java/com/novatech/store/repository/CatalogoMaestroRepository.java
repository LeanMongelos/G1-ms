package com.novatech.store.repository;

import com.novatech.store.entity.CatalogoMaestro;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CatalogoMaestroRepository extends JpaRepository<CatalogoMaestro, Integer> {

    List<CatalogoMaestro> findByTipoOrderByOrdenAscNombreAsc(String tipo);
}
