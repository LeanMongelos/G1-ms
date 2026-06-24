package com.novatech.store.controller;

import com.novatech.store.entity.PerfilCliente;
import com.novatech.store.service.ClienteMetricasService;
import com.novatech.store.service.PerfilClienteService;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/perfiles")
/**
 * Controlador REST `PerfilClienteController`: expone endpoints HTTP JSON para PerfilCliente. Ruta base en `@RequestMapping` de la clase.
 */
public class PerfilClienteController {

    private final PerfilClienteService service;
    private final ClienteMetricasService metricasService;

    public PerfilClienteController(PerfilClienteService service, ClienteMetricasService metricasService) {
        this.service = service;
        this.metricasService = metricasService;
    }

    @GetMapping
    public List<PerfilCliente> listar(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String tipo) {
        if (q != null || tipo != null) {
            return service.listar(q, tipo);
        }
        return service.listar();
    }

    @GetMapping("/{id}")
    public PerfilCliente obtener(@PathVariable Integer id) {
        return service.obtener(id);
    }

    @GetMapping("/{id}/metricas")
    public Map<String, Object> metricas(@PathVariable Integer id) {
        return metricasService.metricas(id);
    }

    @GetMapping("/{id}/historial")
    public Map<String, Object> historial(@PathVariable Integer id) {
        return metricasService.historial(id);
    }

    @PostMapping
    public ResponseEntity<PerfilCliente> crear(@RequestBody PerfilCliente perfil) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(perfil));
    }

    @PutMapping("/{id}")
    public PerfilCliente actualizar(@PathVariable Integer id, @RequestBody PerfilCliente perfil) {
        return service.actualizar(id, perfil);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable Integer id) {
        service.desactivar(id);
        return ResponseEntity.noContent().build();
    }
}
