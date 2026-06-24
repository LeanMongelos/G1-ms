package com.novatech.store.controller;

import com.novatech.store.entity.DetallePedido;
import com.novatech.store.service.DetallePedidoService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Controller de los renglones del pedido. Todas las rutas empiezan con /detalle-pedidos.
@RestController
@RequestMapping("/detalle-pedidos")
/**
 * Controlador REST `DetallePedidoController`: expone endpoints HTTP JSON para DetallePedido. Ruta base en `@RequestMapping` de la clase.
 */
public class DetallePedidoController {

    private final DetallePedidoService service;

    public DetallePedidoController(DetallePedidoService service) {
        this.service = service;
    }

    // GET /detalle-pedidos -> lista de renglones de pedido.
    @GetMapping
    public List<DetallePedido> listar() {
        return service.listar();
    }

    // GET /detalle-pedidos/5 -> un renglon por id.
    @GetMapping("/{id}")
    public DetallePedido obtener(@PathVariable Integer id) {
        return service.obtener(id);
    }

    // POST /detalle-pedidos -> crea un renglon.
    @PostMapping
    public ResponseEntity<DetallePedido> crear(@RequestBody DetallePedido detalle) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(detalle));
    }

    // PUT /detalle-pedidos/5 -> actualiza un renglon.
    @PutMapping("/{id}")
    public DetallePedido actualizar(@PathVariable Integer id, @RequestBody DetallePedido detalle) {
        return service.actualizar(id, detalle);
    }

    // DELETE /detalle-pedidos/5 -> borra un renglon.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
