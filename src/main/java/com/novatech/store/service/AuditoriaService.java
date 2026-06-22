package com.novatech.store.service;

import com.novatech.store.entity.RegistroAuditoria;
import com.novatech.store.repository.RegistroAuditoriaRepository;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

@Service
public class AuditoriaService {

    private final RegistroAuditoriaRepository repository;

    public AuditoriaService(RegistroAuditoriaRepository repository) {
        this.repository = repository;
    }

    public void registrar(String usuario, String modulo, String accion, String detalle,
                          String entidad, String entidadId, String ip,
                          String antes, String despues) {
        RegistroAuditoria r = new RegistroAuditoria();
        r.setFecha(LocalDateTime.now());
        r.setUsuarioNombre(usuario != null ? usuario : "sistema");
        r.setModulo(modulo);
        r.setAccion(accion);
        r.setDetalle(detalle);
        r.setEntidad(entidad);
        r.setEntidadId(entidadId);
        r.setIp(ip);
        r.setDatosAntes(antes);
        r.setDatosDespues(despues);
        repository.save(r);
    }
}
