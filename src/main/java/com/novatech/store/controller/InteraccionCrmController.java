package com.novatech.store.controller;

import com.novatech.store.entity.InteraccionCrm;
import com.novatech.store.service.InteraccionCrmService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/interacciones")
/**
 * Controlador REST `InteraccionCrmController`: expone endpoints HTTP JSON para InteraccionCrm. Ruta base en `@RequestMapping` de la clase.
 */
public class InteraccionCrmController {

    private final InteraccionCrmService service;

    public InteraccionCrmController(InteraccionCrmService service) {
        this.service = service;
    }

    @GetMapping
    public List<InteraccionCrm> listar(@RequestParam(required = false) Integer clienteId) {
        if (clienteId != null) {
            return service.listarPorCliente(clienteId);
        }
        return service.listar();
    }

    @GetMapping("/{id}")
    public InteraccionCrm obtener(@PathVariable Integer id) {
        return service.obtener(id);
    }

    @PostMapping
    public ResponseEntity<InteraccionCrm> crear(@RequestBody InteraccionCrm interaccion) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(interaccion));
    }

    @PutMapping("/{id}")
    public InteraccionCrm actualizar(@PathVariable Integer id, @RequestBody InteraccionCrm interaccion) {
        return service.actualizar(id, interaccion);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
