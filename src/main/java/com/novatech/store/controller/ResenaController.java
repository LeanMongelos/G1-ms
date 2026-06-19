package com.novatech.store.controller;

import com.novatech.store.entity.Resena;
import com.novatech.store.service.ResenaService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Controller de resenas. Todas las rutas empiezan con /resenas.
@RestController
@RequestMapping("/resenas")
public class ResenaController {

    private final ResenaService service;

    public ResenaController(ResenaService service) {
        this.service = service;
    }

    // GET /resenas -> lista de resenas.
    // Tambien acepta un filtro opcional: /resenas?productoId=2 trae las resenas de ese producto.
    @GetMapping
    public List<Resena> listar(@RequestParam(required = false) Integer productoId) {
        if (productoId != null) {
            return service.porProducto(productoId);
        }
        return service.listar();
    }

    // GET /resenas/5 -> una resena por id.
    @GetMapping("/{id}")
    public Resena obtener(@PathVariable Integer id) {
        return service.obtener(id);
    }

    // POST /resenas -> crea una resena.
    @PostMapping
    public ResponseEntity<Resena> crear(@RequestBody Resena resena) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(resena));
    }

    // PUT /resenas/5 -> actualiza una resena.
    @PutMapping("/{id}")
    public Resena actualizar(@PathVariable Integer id, @RequestBody Resena resena) {
        return service.actualizar(id, resena);
    }

    // DELETE /resenas/5 -> borra una resena.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
