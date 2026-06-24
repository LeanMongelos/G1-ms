package com.novatech.store.controller;

import com.novatech.store.entity.CatalogoMaestro;
import com.novatech.store.service.AuditoriaService;
import com.novatech.store.service.CatalogoConfigService;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/config/catalogos")
/**
 * Controlador REST `CatalogoConfigController`: expone endpoints HTTP JSON para CatalogoConfig. Ruta base en `@RequestMapping` de la clase.
 */
public class CatalogoConfigController {

    private final CatalogoConfigService service;
    private final AuditoriaService auditoria;

    public CatalogoConfigController(CatalogoConfigService service, AuditoriaService auditoria) {
        this.service = service;
        this.auditoria = auditoria;
    }

    @GetMapping
    public Map<String, List<CatalogoMaestro>> listar() {
        return service.listarTodo();
    }

    @PostMapping("/{tipo}")
    public ResponseEntity<CatalogoMaestro> crear(@PathVariable String tipo, @RequestBody CatalogoMaestro item) {
        CatalogoMaestro creado = service.crear(tipo.toUpperCase(), item);
        auditoria.registrar("admin", "catalogos", "CREAR", item.getNombre(),
                "CatalogoMaestro", String.valueOf(creado.getIdCatalogo()), null, null, null);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PatchMapping("/{id}")
    public CatalogoMaestro actualizar(@PathVariable Integer id, @RequestBody CatalogoMaestro item) {
        return service.actualizar(id, item);
    }
}
