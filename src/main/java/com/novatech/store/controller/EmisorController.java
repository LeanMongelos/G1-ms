package com.novatech.store.controller;

import com.novatech.store.entity.Emisor;
import com.novatech.store.service.AuditoriaService;
import com.novatech.store.service.EmisorService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/config/emisores")
public class EmisorController {

    private final EmisorService service;
    private final AuditoriaService auditoria;

    public EmisorController(EmisorService service, AuditoriaService auditoria) {
        this.service = service;
        this.auditoria = auditoria;
    }

    @GetMapping
    public List<Emisor> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Emisor obtener(@PathVariable Integer id) {
        return service.obtener(id);
    }

    @PostMapping
    public ResponseEntity<Emisor> crear(@RequestBody Emisor emisor) {
        Emisor creado = service.crear(emisor);
        auditoria.registrar("admin", "emisores", "CREAR", "Alta emisor " + creado.getRazonSocial(),
                "Emisor", String.valueOf(creado.getIdEmisor()), null, null, null);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    public Emisor actualizar(@PathVariable Integer id, @RequestBody Emisor emisor) {
        Emisor actualizado = service.actualizar(id, emisor);
        auditoria.registrar("admin", "emisores", "ACTUALIZAR", "Emisor " + id,
                "Emisor", String.valueOf(id), null, null, null);
        return actualizado;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        service.eliminar(id);
        auditoria.registrar("admin", "emisores", "ELIMINAR", "Emisor " + id,
                "Emisor", String.valueOf(id), null, null, null);
        return ResponseEntity.noContent().build();
    }
}
