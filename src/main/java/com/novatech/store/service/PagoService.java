package com.novatech.store.service;

import com.novatech.store.entity.Pago;
import com.novatech.store.exception.ResourceNotFoundException;
import com.novatech.store.repository.PagoRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

// Logica de negocio para los pagos.
@Service
public class PagoService {

    private final PagoRepository repository;

    public PagoService(PagoRepository repository) {
        this.repository = repository;
    }

    // Trae todos los pagos.
    public List<Pago> listar() {
        return repository.findAll();
    }

    // Trae un pago por su id. Si no existe, lanza error 404.
    public Pago obtener(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado: " + id));
    }

    // Crea un pago nuevo. Si no viene la fecha, ponemos la de ahora.
    public Pago crear(Pago pago) {
        pago.setIdPago(null);
        if (pago.getFechaPago() == null) {
            pago.setFechaPago(LocalDateTime.now());
        }
        // Si no viene estado, asumimos que el pago quedo aprobado.
        if (pago.getEstado() == null || pago.getEstado().isBlank()) {
            pago.setEstado("APROBADO");
        }
        return repository.save(pago);
    }

    // Actualiza un pago existente.
    public Pago actualizar(Integer id, Pago datos) {
        Pago pago = obtener(id);
        pago.setPedido(datos.getPedido());
        pago.setMonto(datos.getMonto());
        pago.setMetodo(datos.getMetodo());
        pago.setProveedorBilletera(datos.getProveedorBilletera());
        pago.setReferencia(datos.getReferencia());
        pago.setEstado(datos.getEstado());
        pago.setAprobadoPor(datos.getAprobadoPor());
        if (datos.getFechaPago() != null) {
            pago.setFechaPago(datos.getFechaPago());
        }
        return repository.save(pago);
    }

    // Borra un pago por su id.
    public void eliminar(Integer id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Pago no encontrado: " + id);
        }
        repository.deleteById(id);
    }
}
