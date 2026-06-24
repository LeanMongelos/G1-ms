package com.novatech.store.repository;

import com.novatech.store.entity.Envio;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

// Repositorio para los envios.
/**
 * Repositorio JPA `EnvioRepository`: consultas y persistencia de entidad Envio en MySQL.
 */
public interface EnvioRepository extends JpaRepository<Envio, Integer> {

    List<Envio> findByPedidoIdPedido(Integer idPedido);
}
