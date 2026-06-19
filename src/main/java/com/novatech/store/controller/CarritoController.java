package com.novatech.store.controller;

import com.novatech.store.entity.Carrito;
import com.novatech.store.service.CarritoService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Controller de carritos. Todas las rutas empiezan con /carritos.
@RestController
@RequestMapping("/carritos")
public class CarritoController {

    private final CarritoService service;

    public CarritoController(CarritoService service) {
        this.service = service;
    }

    // GET /carritos -> lista de carritos.
    @GetMapping
    public List<Carrito> listar() {
        return service.listar();
    }

    // GET /carritos/5 -> un carrito por id.
    @GetMapping("/{id}")
    public Carrito obtener(@PathVariable Integer id) {
        return service.obtener(id);
    }

    // POST /carritos -> crea un carrito.
    @PostMapping
    public ResponseEntity<Carrito> crear(@RequestBody Carrito carrito) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(carrito));
    }

    // PUT /carritos/5 -> actualiza un carrito.
    @PutMapping("/{id}")
    public Carrito actualizar(@PathVariable Integer id, @RequestBody Carrito carrito) {
        return service.actualizar(id, carrito);
    }

    // DELETE /carritos/5 -> borra un carrito.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
