package com.novatech.store.service;

import com.novatech.store.entity.PlanCuotas;
import com.novatech.store.exception.ResourceNotFoundException;
import com.novatech.store.repository.PlanCuotasRepository;
import java.util.List;
import org.springframework.stereotype.Service;

// Logica de negocio para los planes de cuotas.
@Service
public class PlanCuotasService {

    private final PlanCuotasRepository repository;

    public PlanCuotasService(PlanCuotasRepository repository) {
        this.repository = repository;
    }

    // Trae todos los planes de cuotas.
    public List<PlanCuotas> listar() {
        return repository.findAll();
    }

    // Trae un plan por su id. Si no existe, lanza error 404.
    public PlanCuotas obtener(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan de cuotas no encontrado: " + id));
    }

    // Crea un plan nuevo. Si no viene estado, lo dejamos "ACTIVO".
    public PlanCuotas crear(PlanCuotas plan) {
        plan.setIdPlan(null);
        if (plan.getEstado() == null || plan.getEstado().isBlank()) {
            plan.setEstado("ACTIVO");
        }
        return repository.save(plan);
    }

    // Actualiza un plan existente.
    public PlanCuotas actualizar(Integer id, PlanCuotas datos) {
        PlanCuotas plan = obtener(id);
        plan.setCliente(datos.getCliente());
        plan.setPedido(datos.getPedido());
        plan.setCantidadCuotas(datos.getCantidadCuotas());
        plan.setInteres(datos.getInteres());
        plan.setEstado(datos.getEstado());
        return repository.save(plan);
    }

    // Borra un plan por su id.
    public void eliminar(Integer id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Plan de cuotas no encontrado: " + id);
        }
        repository.deleteById(id);
    }
}
