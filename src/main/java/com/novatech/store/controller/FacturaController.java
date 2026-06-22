package com.novatech.store.controller;

import com.novatech.store.dto.GenerarFacturaRequest;
import com.novatech.store.entity.Factura;
import com.novatech.store.service.FacturaService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/facturas")
public class FacturaController {

    private final FacturaService service;

    public FacturaController(FacturaService service) {
        this.service = service;
    }

    @GetMapping
    public List<Factura> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Factura obtener(@PathVariable Integer id) {
        return service.obtener(id);
    }

    @PostMapping
    public ResponseEntity<Factura> crear(@RequestBody Factura factura) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(factura));
    }

    @PostMapping("/generar")
    public ResponseEntity<Factura> generar(@RequestBody GenerarFacturaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.generar(request));
    }

    @PostMapping("/generar-presupuesto/{presupuestoId}")
    public ResponseEntity<Factura> generarDesdePresupuesto(@PathVariable Integer presupuestoId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.generarDesdePresupuesto(presupuestoId));
    }

    @PostMapping("/generar-legacy")
    public ResponseEntity<Factura> generarLegacy(@RequestParam Integer pedidoId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.generarDesdePedido(pedidoId));
    }

    @PutMapping("/{id}")
    public Factura actualizar(@PathVariable Integer id, @RequestBody Factura factura) {
        return service.actualizar(id, factura);
    }

    @PostMapping("/{id}/emitir")
    public Factura emitir(@PathVariable Integer id) {
        return service.emitir(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
