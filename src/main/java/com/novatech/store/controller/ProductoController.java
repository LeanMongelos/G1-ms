package com.novatech.store.controller;

import com.novatech.store.entity.Producto;
import com.novatech.store.service.ProductoService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Controller de productos. Todas las rutas empiezan con /productos.
@RestController
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoService service;

    public ProductoController(ProductoService service) {
        this.service = service;
    }

    // GET /productos -> lista de productos.
    // Tambien acepta filtros opcionales por la URL:
    //   /productos?nombre=teclado     -> busca por nombre
    //   /productos?categoriaId=2      -> filtra por categoria
    // @RequestParam(required = false) significa que el parametro puede venir o no.
    @GetMapping
    public List<Producto> listar(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Integer categoriaId) {
        if (nombre != null) {
            return service.buscarPorNombre(nombre);
        }
        if (categoriaId != null) {
            return service.porCategoria(categoriaId);
        }
        return service.listar();
    }

    // GET /productos/5 -> un producto por id.
    @GetMapping("/{id}")
    public Producto obtener(@PathVariable Integer id) {
        return service.obtener(id);
    }

    // POST /productos -> crea un producto.
    @PostMapping
    public ResponseEntity<Producto> crear(@RequestBody Producto producto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(producto));
    }

    // PUT /productos/5 -> actualiza un producto.
    @PutMapping("/{id}")
    public Producto actualizar(@PathVariable Integer id, @RequestBody Producto producto) {
        return service.actualizar(id, producto);
    }

    // DELETE /productos/5 -> borra un producto.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
