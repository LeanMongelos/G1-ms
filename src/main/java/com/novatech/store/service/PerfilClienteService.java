package com.novatech.store.service;

import com.novatech.store.entity.PerfilCliente;
import com.novatech.store.exception.ResourceNotFoundException;
import com.novatech.store.repository.PerfilClienteRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Servicio `PerfilClienteService`: reglas de negocio, transacciones y orquestación de PerfilCliente. Los controllers delegan aquí; no accede HTTP directamente.
 */
@Service
public class PerfilClienteService {

    private final PerfilClienteRepository repository;

    public PerfilClienteService(PerfilClienteRepository repository) {
        this.repository = repository;
    }

    public List<PerfilCliente> listar(String q, String tipo) {
        String busqueda = (q == null || q.isBlank()) ? null : q.trim();
        String filtroTipo = (tipo == null || tipo.isBlank()) ? null : tipo.trim();
        return repository.buscarActivos(busqueda, filtroTipo);
    }

    public List<PerfilCliente> listar() {
        return repository.buscarActivos(null, null);
    }

    public PerfilCliente obtener(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil de cliente no encontrado: " + id));
    }

    public PerfilCliente crear(PerfilCliente perfil) {
        perfil.setIdCliente(null);
        if (perfil.getActivo() == null) {
            perfil.setActivo(true);
        }
        if (perfil.getCreadoEn() == null) {
            perfil.setCreadoEn(LocalDateTime.now());
        }
        return repository.save(perfil);
    }

    public PerfilCliente actualizar(Integer id, PerfilCliente datos) {
        PerfilCliente perfil = obtener(id);
        if (datos.getUsuario() != null) perfil.setUsuario(datos.getUsuario());
        if (datos.getDireccion() != null) perfil.setDireccion(datos.getDireccion());
        if (datos.getCiudad() != null) perfil.setCiudad(datos.getCiudad());
        if (datos.getTelefono() != null) perfil.setTelefono(datos.getTelefono());
        if (datos.getCuit() != null) perfil.setCuit(datos.getCuit());
        if (datos.getContacto() != null) perfil.setContacto(datos.getContacto());
        if (datos.getHistorialCrediticio() != null) perfil.setHistorialCrediticio(datos.getHistorialCrediticio());
        if (datos.getTipoCliente() != null) perfil.setTipoCliente(datos.getTipoCliente());
        if (datos.getCondicionIva() != null) perfil.setCondicionIva(datos.getCondicionIva());
        if (datos.getCondicionPago() != null) perfil.setCondicionPago(datos.getCondicionPago());
        if (datos.getLimiteCredito() != null) perfil.setLimiteCredito(datos.getLimiteCredito());
        if (datos.getSegmento() != null) perfil.setSegmento(datos.getSegmento());
        if (datos.getSitioWeb() != null) perfil.setSitioWeb(datos.getSitioWeb());
        if (datos.getNotas() != null) perfil.setNotas(datos.getNotas());
        if (datos.getLat() != null) perfil.setLat(datos.getLat());
        if (datos.getLng() != null) perfil.setLng(datos.getLng());
        return repository.save(perfil);
    }

    public void desactivar(Integer id) {
        PerfilCliente perfil = obtener(id);
        perfil.setActivo(false);
        repository.save(perfil);
    }

    /** @deprecated usar desactivar */
    public void eliminar(Integer id) {
        desactivar(id);
    }
}
