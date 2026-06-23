package com.novatech.store.repository;

import com.novatech.store.entity.SolicitudDevolucion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SolicitudDevolucionRepository extends JpaRepository<SolicitudDevolucion, Integer> {

    List<SolicitudDevolucion> findByCliente_Usuario_IdUsuarioOrderByFechaCreacionDesc(Integer idUsuario);
}
