package com.novatech.store.service;

import com.novatech.store.entity.Emisor;
import com.novatech.store.repository.EmisorRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Servicio `EmisorService`: reglas de negocio, transacciones y orquestación de Emisor. Los controllers delegan aquí; no accede HTTP directamente.
 */
@Service
public class EmisorService {

    private final EmisorRepository repository;

    public EmisorService(EmisorRepository repository) {
        this.repository = repository;
    }

    public List<Emisor> listar() {
        return repository.findAllByOrderByRazonSocialAsc();
    }

    public Emisor obtener(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Emisor no encontrado"));
    }

    @Transactional
    public Emisor crear(Emisor emisor) {
        if (Boolean.TRUE.equals(emisor.getEsDefault())) {
            quitarDefault();
        }
        if (emisor.getAmbiente() == null) {
            emisor.setAmbiente("HOMOLOGACION");
        }
        return repository.save(emisor);
    }

    @Transactional
    public Emisor actualizar(Integer id, Emisor datos) {
        Emisor existente = obtener(id);
        existente.setRazonSocial(datos.getRazonSocial());
        existente.setCuit(datos.getCuit());
        existente.setCondicionIva(datos.getCondicionIva());
        existente.setIibb(datos.getIibb());
        existente.setDomicilio(datos.getDomicilio());
        existente.setPuntoVenta(datos.getPuntoVenta());
        existente.setAmbiente(datos.getAmbiente());
        existente.setCertificadoNombre(datos.getCertificadoNombre());
        existente.setCertificadoVencimiento(datos.getCertificadoVencimiento());
        if (Boolean.TRUE.equals(datos.getEsDefault())) {
            quitarDefault();
            existente.setEsDefault(true);
        }
        return repository.save(existente);
    }

    @Transactional
    public void eliminar(Integer id) {
        repository.delete(obtener(id));
    }

    private void quitarDefault() {
        repository.findByEsDefaultTrue().ifPresent(e -> {
            e.setEsDefault(false);
            repository.save(e);
        });
    }
}
