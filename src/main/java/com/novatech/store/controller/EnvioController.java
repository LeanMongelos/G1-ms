package com.novatech.store.controller;

import com.novatech.store.entity.Envio;
import com.novatech.store.service.EnvioService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Controller de envios. Todas las rutas empiezan con /envios.
@RestController
@RequestMapping("/envios")
public class EnvioController {

    private final EnvioService service;

    public EnvioController(EnvioService service) {
        this.service = service;
    }

    // GET /envios -> lista de envios.
    @GetMapping
    public List<Envio> listar() {
        return service.listar();
    }

    // GET /envios/5 -> un envio por id.
    @GetMapping("/{id}")
    public Envio obtener(@PathVariable Integer id) {
        return service.obtener(id);
    }

    // POST /envios -> crea un envio.
    @PostMapping
    public ResponseEntity<Envio> crear(@Valid @RequestBody Envio envio) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(envio));
    }

    // PUT /envios/5 -> actualiza un envio.
    @PutMapping("/{id}")
    public Envio actualizar(@PathVariable Integer id, @RequestBody Envio envio) {
        return service.actualizar(id, envio);
    }

    // DELETE /envios/5 -> borra un envio.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
