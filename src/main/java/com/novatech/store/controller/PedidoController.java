package com.novatech.store.controller;

import com.novatech.store.entity.Pedido;
import com.novatech.store.service.PedidoService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Controller de pedidos. Todas las rutas empiezan con /pedidos.
@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService service;

    public PedidoController(PedidoService service) {
        this.service = service;
    }

    // GET /pedidos -> lista de pedidos.
    @GetMapping
    public List<Pedido> listar() {
        return service.listar();
    }

    // GET /pedidos/5 -> un pedido por id.
    @GetMapping("/{id}")
    public Pedido obtener(@PathVariable Integer id) {
        return service.obtener(id);
    }

    // POST /pedidos -> crea un pedido.
    @PostMapping
    public ResponseEntity<Pedido> crear(@Valid @RequestBody Pedido pedido) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(pedido));
    }

    // PUT /pedidos/5 -> actualiza un pedido.
    @PutMapping("/{id}")
    public Pedido actualizar(@PathVariable Integer id, @RequestBody Pedido pedido) {
        return service.actualizar(id, pedido);
    }

    // DELETE /pedidos/5 -> borra un pedido.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
