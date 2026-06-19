package com.novatech.store.controller;

import com.novatech.store.entity.Usuario;
import com.novatech.store.service.UsuarioService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Controller de usuarios. Todas las rutas empiezan con /usuarios.
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    // GET /usuarios -> lista de usuarios.
    @GetMapping
    public List<Usuario> listar() {
        return service.listar();
    }

    // GET /usuarios/5 -> un usuario por id.
    @GetMapping("/{id}")
    public Usuario obtener(@PathVariable Integer id) {
        return service.obtener(id);
    }

    // POST /usuarios -> crea un usuario.
    @PostMapping
    public ResponseEntity<Usuario> crear(@RequestBody Usuario usuario) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(usuario));
    }

    // PUT /usuarios/5 -> actualiza un usuario.
    @PutMapping("/{id}")
    public Usuario actualizar(@PathVariable Integer id, @RequestBody Usuario usuario) {
        return service.actualizar(id, usuario);
    }

    // DELETE /usuarios/5 -> borra un usuario.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
