package com.novatech.store.controller;

import com.novatech.store.entity.Campana;
import com.novatech.store.entity.MensajeCliente;
import com.novatech.store.service.CampanaService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/campanas")
/**
 * Controlador REST `CampanaController`: expone endpoints HTTP JSON para Campana. Ruta base en `@RequestMapping` de la clase.
 */
public class CampanaController {

    private final CampanaService service;

    public CampanaController(CampanaService service) {
        this.service = service;
    }

    @GetMapping
    public List<Campana> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Campana obtener(@PathVariable Integer id) {
        return service.obtener(id);
    }

    @PostMapping
    public ResponseEntity<Campana> crear(@RequestBody Campana campana) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(campana));
    }

    @PutMapping("/{id}")
    public Campana actualizar(@PathVariable Integer id, @RequestBody Campana campana) {
        return service.actualizar(id, campana);
    }

    @PostMapping("/{id}/enviar")
    public Campana enviar(@PathVariable Integer id) {
        return service.enviar(id);
    }

    @GetMapping("/{id}/mensajes")
    public List<MensajeCliente> listarMensajes(@PathVariable Integer id) {
        return service.listarMensajes(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
