package com.novatech.store.controller;

import com.novatech.store.entity.Cuota;
import com.novatech.store.service.CuotaService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cuotas")
public class CuotaController {

    private final CuotaService service;

    public CuotaController(CuotaService service) {
        this.service = service;
    }

    @GetMapping
    public List<Cuota> listar() {
        return service.listar();
    }

    @GetMapping("/vencidas")
    public List<Cuota> vencidas() {
        return service.listarVencidas();
    }

    @GetMapping("/por-vencer")
    public List<Cuota> porVencer(@RequestParam(defaultValue = "7") int dias) {
        return service.listarPorVencer(dias);
    }

    @GetMapping("/plan/{idPlan}")
    public List<Cuota> porPlan(@PathVariable Integer idPlan) {
        return service.listarPorPlan(idPlan);
    }

    @GetMapping("/{id}")
    public Cuota obtener(@PathVariable Integer id) {
        return service.obtener(id);
    }

    @PostMapping("/{id}/pagar")
    public Cuota pagar(@PathVariable Integer id) {
        return service.marcarPagada(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
