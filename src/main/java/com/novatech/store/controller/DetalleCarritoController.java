package com.novatech.store.controller;

import com.novatech.store.entity.DetalleCarrito;
import com.novatech.store.service.DetalleCarritoService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Controller de los renglones del carrito. Todas las rutas empiezan con /detalle-carritos.
@RestController
@RequestMapping("/detalle-carritos")
/**
 * Controlador REST `DetalleCarritoController`: expone endpoints HTTP JSON para DetalleCarrito. Ruta base en `@RequestMapping` de la clase.
 */
public class DetalleCarritoController {

    private final DetalleCarritoService service;

    public DetalleCarritoController(DetalleCarritoService service) {
        this.service = service;
    }

    // GET /detalle-carritos -> lista de renglones de carrito.
    @GetMapping
    public List<DetalleCarrito> listar() {
        return service.listar();
    }

    // GET /detalle-carritos/5 -> un renglon por id.
    @GetMapping("/{id}")
    public DetalleCarrito obtener(@PathVariable Integer id) {
        return service.obtener(id);
    }

    // POST /detalle-carritos -> crea un renglon.
    @PostMapping
    public ResponseEntity<DetalleCarrito> crear(@RequestBody DetalleCarrito detalle) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(detalle));
    }

    // PUT /detalle-carritos/5 -> actualiza un renglon.
    @PutMapping("/{id}")
    public DetalleCarrito actualizar(@PathVariable Integer id, @RequestBody DetalleCarrito detalle) {
        return service.actualizar(id, detalle);
    }

    // DELETE /detalle-carritos/5 -> borra un renglon.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
