package com.novatech.store.controller;

import com.novatech.store.dto.PresupuestoRequest;
import com.novatech.store.entity.Presupuesto;
import com.novatech.store.service.PresupuestoService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/presupuestos")
public class PresupuestoController {

    private final PresupuestoService service;

    public PresupuestoController(PresupuestoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Presupuesto> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Presupuesto obtener(@PathVariable Integer id) {
        return service.obtener(id);
    }

    @PostMapping
    public ResponseEntity<Presupuesto> crear(@Valid @RequestBody PresupuestoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
    }

    @PutMapping("/{id}")
    public Presupuesto actualizar(@PathVariable Integer id, @Valid @RequestBody PresupuestoRequest request) {
        return service.actualizar(id, request);
    }

    @PostMapping("/{id}/estado/{estado}")
    public Presupuesto cambiarEstado(@PathVariable Integer id, @PathVariable String estado) {
        return service.cambiarEstado(id, estado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
