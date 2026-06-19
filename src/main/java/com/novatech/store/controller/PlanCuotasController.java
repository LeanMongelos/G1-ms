package com.novatech.store.controller;

import com.novatech.store.entity.PlanCuotas;
import com.novatech.store.service.PlanCuotasService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Controller de planes de cuotas. Todas las rutas empiezan con /planes.
@RestController
@RequestMapping("/planes")
public class PlanCuotasController {

    private final PlanCuotasService service;

    public PlanCuotasController(PlanCuotasService service) {
        this.service = service;
    }

    // GET /planes -> lista de planes de cuotas.
    @GetMapping
    public List<PlanCuotas> listar() {
        return service.listar();
    }

    // GET /planes/5 -> un plan por id.
    @GetMapping("/{id}")
    public PlanCuotas obtener(@PathVariable Integer id) {
        return service.obtener(id);
    }

    // POST /planes -> crea un plan.
    @PostMapping
    public ResponseEntity<PlanCuotas> crear(@RequestBody PlanCuotas plan) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(plan));
    }

    // PUT /planes/5 -> actualiza un plan.
    @PutMapping("/{id}")
    public PlanCuotas actualizar(@PathVariable Integer id, @RequestBody PlanCuotas plan) {
        return service.actualizar(id, plan);
    }

    // DELETE /planes/5 -> borra un plan.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
