package com.novatech.store.repository;

import com.novatech.store.entity.PlantillaImpresion;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio JPA `PlantillaImpresionRepository`: consultas y persistencia de entidad PlantillaImpresion en MySQL.
 */
public interface PlantillaImpresionRepository extends JpaRepository<PlantillaImpresion, Integer> {

    List<PlantillaImpresion> findAllByOrderByTipoAscNombreAsc();

    Optional<PlantillaImpresion> findFirstByTipoAndEsDefaultTrueAndActivoTrueOrderByIdPlantillaAsc(String tipo);
}
