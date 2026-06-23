package com.novatech.store.controller;

import com.novatech.store.dto.ListaPrecioDetalleRequest;
import com.novatech.store.dto.ListaPrecioUpdateRequest;
import com.novatech.store.dto.PrecioResueltoDto;
import com.novatech.store.entity.ListaPrecio;
import com.novatech.store.entity.ListaPrecioDetalle;
import com.novatech.store.service.ListaPrecioService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/listas-precios")
public class ListaPrecioController {

    private final ListaPrecioService service;

    public ListaPrecioController(ListaPrecioService service) {
        this.service = service;
    }

    @GetMapping
    public List<ListaPrecio> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public ListaPrecio obtener(@PathVariable Integer id) {
        return service.obtener(id);
    }

    @GetMapping("/codigo/{codigo}")
    public ListaPrecio porCodigo(@PathVariable String codigo) {
        return service.porCodigo(codigo);
    }

    @PutMapping("/{id}")
    public ListaPrecio actualizar(@PathVariable Integer id, @Valid @RequestBody ListaPrecioUpdateRequest body) {
        return service.actualizar(id, body);
    }

    @GetMapping("/{id}/detalles")
    public List<ListaPrecioDetalle> detalles(@PathVariable Integer id) {
        return service.listarDetalles(id);
    }

    @PostMapping("/{id}/detalles")
    public ResponseEntity<ListaPrecioDetalle> guardarDetalle(
            @PathVariable Integer id, @Valid @RequestBody ListaPrecioDetalleRequest body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardarDetalle(id, body));
    }

    @DeleteMapping("/{id}/detalles/{idDetalle}")
    public ResponseEntity<Void> eliminarDetalle(@PathVariable Integer id, @PathVariable Integer idDetalle) {
        service.eliminarDetalle(id, idDetalle);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/resolver")
    public PrecioResueltoDto resolver(
            @RequestParam Integer productoId,
            @RequestParam(required = false) String lista,
            @RequestParam(required = false) String canal,
            @RequestParam(required = false) String tipoCliente) {
        if (lista != null && !lista.isBlank()) {
            return service.resolverPrecio(productoId, lista);
        }
        String codigo = service.resolverCodigoLista(canal, tipoCliente);
        return service.resolverPrecio(productoId, codigo);
    }

    @GetMapping("/codigo/{codigo}/precio/{productoId}")
    public PrecioResueltoDto precioProducto(@PathVariable String codigo, @PathVariable Integer productoId) {
        return service.resolverPrecio(productoId, codigo);
    }
}
