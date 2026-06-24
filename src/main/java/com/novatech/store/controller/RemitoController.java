package com.novatech.store.controller;

import com.novatech.store.dto.RemitoRequest;
import com.novatech.store.entity.Remito;
import com.novatech.store.service.RemitoService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/remitos")
/**
 * Controlador REST `RemitoController`: expone endpoints HTTP JSON para Remito. Ruta base en `@RequestMapping` de la clase.
 */
public class RemitoController {

    private final RemitoService service;

    public RemitoController(RemitoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Remito> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Remito obtener(@PathVariable Integer id) {
        return service.obtener(id);
    }

    @PostMapping
    public ResponseEntity<Remito> crear(@Valid @RequestBody RemitoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
    }

    @PostMapping("/generar-pedido/{pedidoId}")
    public ResponseEntity<Remito> generarDesdePedido(
            @PathVariable Integer pedidoId,
            @RequestParam(required = false) String direccionEntrega) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.generarDesdePedido(pedidoId, direccionEntrega));
    }

    @PostMapping("/generar-presupuesto/{presupuestoId}")
    public ResponseEntity<Remito> generarDesdePresupuesto(
            @PathVariable Integer presupuestoId,
            @RequestParam(required = false) String direccionEntrega) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.generarDesdePresupuesto(presupuestoId, direccionEntrega));
    }

    @PutMapping("/{id}")
    public Remito actualizar(@PathVariable Integer id, @Valid @RequestBody RemitoRequest request) {
        return service.actualizar(id, request);
    }

    @PostMapping("/{id}/estado/{estado}")
    public Remito cambiarEstado(@PathVariable Integer id, @PathVariable String estado) {
        return service.cambiarEstado(id, estado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
