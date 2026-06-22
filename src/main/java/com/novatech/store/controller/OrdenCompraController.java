package com.novatech.store.controller;

import com.novatech.store.dto.GenerarOrdenCompraRequest;
import com.novatech.store.entity.OrdenCompra;
import com.novatech.store.service.OrdenCompraService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ordenes-compra")
public class OrdenCompraController {

    private final OrdenCompraService service;

    public OrdenCompraController(OrdenCompraService service) {
        this.service = service;
    }

    @GetMapping
    public List<OrdenCompra> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public OrdenCompra obtener(@PathVariable Integer id) {
        return service.obtener(id);
    }

    @PostMapping("/generar-stock-bajo")
    public ResponseEntity<List<OrdenCompra>> generarStockBajo() {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.generarDesdeStockBajo());
    }

    @PostMapping("/generar")
    public ResponseEntity<List<OrdenCompra>> generar(@RequestBody GenerarOrdenCompraRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.generarParaProductos(req.getProductoIds()));
    }

    @PostMapping("/{id}/enviar")
    public OrdenCompra enviar(@PathVariable Integer id) {
        return service.enviar(id);
    }

    @PostMapping("/{id}/recibir")
    public OrdenCompra recibir(@PathVariable Integer id) {
        return service.recibir(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
