package com.novatech.store.controller;

import com.novatech.store.dto.UsuarioResponse;
import com.novatech.store.entity.Usuario;
import com.novatech.store.security.SecurityUtils;
import com.novatech.store.service.UsuarioService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
/**
 * Controlador REST `UsuarioController`: expone endpoints HTTP JSON para Usuario. Ruta base en `@RequestMapping` de la clase.
 */
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @GetMapping
    public List<UsuarioResponse> listar() {
        SecurityUtils.requerirStaff();
        return service.listar().stream().map(UsuarioResponse::desde).toList();
    }

    @GetMapping("/{id}")
    public UsuarioResponse obtener(@PathVariable Integer id) {
        SecurityUtils.requerirStaff();
        return UsuarioResponse.desde(service.obtener(id));
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> crear(@Valid @RequestBody Usuario usuario) {
        SecurityUtils.requerirStaff();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UsuarioResponse.desde(service.crear(usuario)));
    }

    @PutMapping("/{id}")
    public UsuarioResponse actualizar(@PathVariable Integer id, @RequestBody Usuario usuario) {
        SecurityUtils.requerirStaff();
        return UsuarioResponse.desde(service.actualizar(id, usuario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        SecurityUtils.requerirStaff();
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
