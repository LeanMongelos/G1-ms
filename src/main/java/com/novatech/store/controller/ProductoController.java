package com.novatech.store.controller;

import com.novatech.store.entity.Producto;
import com.novatech.store.service.ProductoService;
import jakarta.validation.Valid;
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
            @RequestParam(required = false) Integer categoriaId,
            @RequestParam(required = false) String listaPrecio,
            @RequestParam(required = false) String canal,
            @RequestParam(required = false) String tipoCliente) {
        List<Producto> base;
        if (nombre != null) {
            base = service.buscarPorNombre(nombre);
        } else if (categoriaId != null) {
            base = service.porCategoria(categoriaId);
        } else {
            base = service.listar();
        }
        if (listaPrecio != null || canal != null || tipoCliente != null) {
            String codigo = listaPrecio;
            if (codigo == null || codigo.isBlank()) {
                codigo = null;
            }
            final String lista = codigo;
            final String c = canal;
            final String t = tipoCliente;
            return base.stream()
                    .map(p -> lista != null
                            ? service.obtenerConPrecioLista(p.getIdProducto(), lista, null, null)
                            : service.obtenerConPrecioLista(p.getIdProducto(), null, c, t))
                    .toList();
        }
        return base;
    }

    @GetMapping("/stock-bajo")
    public List<Producto> stockBajo() {
        return service.listarStockBajo();
    }

    // GET /productos/5 -> un producto por id.
    @GetMapping("/{id}")
    public Producto obtener(
            @PathVariable Integer id,
            @RequestParam(required = false) String listaPrecio,
            @RequestParam(required = false) String canal,
            @RequestParam(required = false) String tipoCliente) {
        if (listaPrecio != null || canal != null || tipoCliente != null) {
            return service.obtenerConPrecioLista(id, listaPrecio, canal, tipoCliente);
        }
        return service.obtener(id);
    }

    // POST /productos -> crea un producto.
    @PostMapping
    public ResponseEntity<Producto> crear(@Valid @RequestBody Producto producto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(producto));
    }

    // PUT /productos/5 -> actualiza un producto.
    @PutMapping("/{id}")
    public Producto actualizar(@PathVariable Integer id, @Valid @RequestBody Producto producto) {
        return service.actualizar(id, producto);
    }

    // DELETE /productos/5 -> borra un producto.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
