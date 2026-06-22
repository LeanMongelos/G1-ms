package com.novatech.store.repository;

import com.novatech.store.entity.Conversacion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversacionRepository extends JpaRepository<Conversacion, Integer> {

    List<Conversacion> findByCanalIgnoreCase(String canal);

    List<Conversacion> findByEstadoIgnoreCase(String estado);

    List<Conversacion> findByCanalIgnoreCaseAndEstadoIgnoreCase(String canal, String estado);
}
