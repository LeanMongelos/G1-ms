package com.novatech.store.repository;

import com.novatech.store.entity.Emisor;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmisorRepository extends JpaRepository<Emisor, Integer> {

    Optional<Emisor> findByEsDefaultTrue();

    List<Emisor> findAllByOrderByRazonSocialAsc();
}
