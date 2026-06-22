package com.novatech.store.controller;

import com.novatech.store.entity.Categoria;
import com.novatech.store.service.CategoriaService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// @RestController = esta clase recibe pedidos HTTP (del navegador o del frontend) y devuelve JSON.
@RestController
// @RequestMapping = todas las rutas de esta clase empiezan con /categorias.
@RequestMapping("/categorias")
public class CategoriaController {

    // El controller usa el service para hacer el trabajo.
    private final CategoriaService service;

    public CategoriaController(CategoriaService service) {
        this.service = service;
    }

    // GET /categorias -> devuelve la lista de categorias.
    @GetMapping
    public List<Categoria> listar() {
        return service.listar();
    }

    // GET /categorias/5 -> devuelve la categoria con id 5.
    // @PathVariable agarra el numero que viene en la URL.
    @GetMapping("/{id}")
    public Categoria obtener(@PathVariable Integer id) {
        return service.obtener(id);
    }

    // POST /categorias -> crea una categoria nueva.
    // @RequestBody convierte el JSON que mandan en un objeto Categoria.
    // Devolvemos el codigo 201 (CREATED) que significa "se creo correctamente".
    @PostMapping
    public ResponseEntity<Categoria> crear(@Valid @RequestBody Categoria categoria) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(categoria));
    }

    // PUT /categorias/5 -> actualiza la categoria con id 5.
    @PutMapping("/{id}")
    public Categoria actualizar(@PathVariable Integer id, @Valid @RequestBody Categoria categoria) {
        return service.actualizar(id, categoria);
    }

    // DELETE /categorias/5 -> borra la categoria con id 5.
    // Devolvemos 204 (NO CONTENT) que significa "salio bien y no hay nada para devolver".
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
