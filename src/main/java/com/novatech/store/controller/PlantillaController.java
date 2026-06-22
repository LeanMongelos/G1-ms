package com.novatech.store.controller;

import com.novatech.store.dto.PlantillaRenderResponse;
import com.novatech.store.entity.PlantillaImpresion;
import com.novatech.store.service.AuditoriaService;
import com.novatech.store.service.PlantillaRenderService;
import com.novatech.store.service.PlantillaService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/config/plantillas")
public class PlantillaController {

    private final PlantillaService service;
    private final PlantillaRenderService renderService;
    private final AuditoriaService auditoria;

    public PlantillaController(PlantillaService service, PlantillaRenderService renderService,
                               AuditoriaService auditoria) {
        this.service = service;
        this.renderService = renderService;
        this.auditoria = auditoria;
    }

    @GetMapping
    public List<PlantillaImpresion> listar() {
        return service.listar();
    }

    @PostMapping
    public ResponseEntity<PlantillaImpresion> crear(@RequestBody PlantillaImpresion plantilla) {
        PlantillaImpresion creada = service.crear(plantilla);
        auditoria.registrar("admin", "plantillas", "CREAR", plantilla.getNombre(),
                "PlantillaImpresion", String.valueOf(creada.getIdPlantilla()), null, null, null);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @PutMapping("/{id}")
    public PlantillaImpresion actualizar(@PathVariable Integer id, @RequestBody PlantillaImpresion plantilla) {
        return service.actualizar(id, plantilla);
    }

    @PostMapping("/{id}/duplicar")
    public PlantillaImpresion duplicar(@PathVariable Integer id) {
        return service.duplicar(id);
    }

    @GetMapping("/{id}/preview")
    public PlantillaRenderResponse preview(@PathVariable Integer id) {
        return renderService.preview(id);
    }

    @GetMapping("/preview/{tipo}")
    public PlantillaRenderResponse previewDefault(@PathVariable String tipo) {
        return renderService.previewDefault(tipo.toUpperCase());
    }

    @GetMapping("/render/FACTURA/{id}")
    public PlantillaRenderResponse renderFactura(@PathVariable Integer id,
                                                 @RequestParam(required = false) Integer plantillaId) {
        return renderService.renderFactura(id, plantillaId);
    }

    @GetMapping("/render/PRESUPUESTO/{id}")
    public PlantillaRenderResponse renderPresupuesto(@PathVariable Integer id,
                                                     @RequestParam(required = false) Integer plantillaId) {
        return renderService.renderPresupuesto(id, plantillaId);
    }

    @GetMapping("/render/REMITO/{id}")
    public PlantillaRenderResponse renderRemito(@PathVariable Integer id,
                                                @RequestParam(required = false) Integer plantillaId) {
        return renderService.renderRemito(id, plantillaId);
    }
}
