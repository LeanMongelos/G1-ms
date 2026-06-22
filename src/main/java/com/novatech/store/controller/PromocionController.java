package com.novatech.store.controller;

import com.novatech.store.entity.Promocion;
import com.novatech.store.service.PromocionService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/promociones")
public class PromocionController {

    private final PromocionService service;

    public PromocionController(PromocionService service) {
        this.service = service;
    }

    @GetMapping
    public List<Promocion> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Promocion obtener(@PathVariable Integer id) {
        return service.obtener(id);
    }

    @PostMapping
    public ResponseEntity<Promocion> crear(@RequestBody Promocion promocion) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(promocion));
    }

    @PutMapping("/{id}")
    public Promocion actualizar(@PathVariable Integer id, @RequestBody Promocion promocion) {
        return service.actualizar(id, promocion);
    }

    @PostMapping("/{id}/activar")
    public Promocion activar(@PathVariable Integer id) {
        return service.activar(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
