package com.novatech.store.controller;

import com.novatech.store.dto.CuotaResumenDto;
import com.novatech.store.entity.Cuota;
import com.novatech.store.service.CuotaService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cuotas")
/**
 * Controlador REST `CuotaController`: expone endpoints HTTP JSON para Cuota. Ruta base en `@RequestMapping` de la clase.
 */
public class CuotaController {

    private final CuotaService service;

    public CuotaController(CuotaService service) {
        this.service = service;
    }

    @GetMapping
    public List<CuotaResumenDto> listar() {
        return service.listar().stream().map(CuotaResumenDto::desde).toList();
    }

    @GetMapping("/vencidas")
    public List<CuotaResumenDto> vencidas() {
        return service.listarVencidas().stream().map(CuotaResumenDto::desde).toList();
    }

    @GetMapping("/por-vencer")
    public List<CuotaResumenDto> porVencer(@RequestParam(defaultValue = "7") int dias) {
        return service.listarPorVencer(dias).stream().map(CuotaResumenDto::desde).toList();
    }

    @GetMapping("/plan/{idPlan}")
    public List<CuotaResumenDto> porPlan(@PathVariable Integer idPlan) {
        return service.listarPorPlan(idPlan).stream().map(CuotaResumenDto::desde).toList();
    }

    @GetMapping("/{id}")
    public CuotaResumenDto obtener(@PathVariable Integer id) {
        return CuotaResumenDto.desde(service.obtener(id));
    }

    @PostMapping("/{id}/pagar")
    public CuotaResumenDto pagar(@PathVariable Integer id) {
        return CuotaResumenDto.desde(service.marcarPagada(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
