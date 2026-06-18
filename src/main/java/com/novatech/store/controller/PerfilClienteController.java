package com.novatech.store.controller;

import com.novatech.store.entity.PerfilCliente;
import com.novatech.store.service.PerfilClienteService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Controller de perfiles de cliente. Todas las rutas empiezan con /perfiles.
@RestController
@RequestMapping("/perfiles")
public class PerfilClienteController {

    private final PerfilClienteService service;

    public PerfilClienteController(PerfilClienteService service) {
        this.service = service;
    }

    // GET /perfiles -> lista de perfiles.
    @GetMapping
    public List<PerfilCliente> listar() {
        return service.listar();
    }

    // GET /perfiles/5 -> un perfil por id.
    @GetMapping("/{id}")
    public PerfilCliente obtener(@PathVariable Integer id) {
        return service.obtener(id);
    }

    // POST /perfiles -> crea un perfil.
    @PostMapping
    public ResponseEntity<PerfilCliente> crear(@RequestBody PerfilCliente perfil) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(perfil));
    }

    // PUT /perfiles/5 -> actualiza un perfil.
    @PutMapping("/{id}")
    public PerfilCliente actualizar(@PathVariable Integer id, @RequestBody PerfilCliente perfil) {
        return service.actualizar(id, perfil);
    }

    // DELETE /perfiles/5 -> borra un perfil.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
