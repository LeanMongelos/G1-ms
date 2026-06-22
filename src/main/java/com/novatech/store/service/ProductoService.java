package com.novatech.store.service;

import com.novatech.store.entity.Producto;
import com.novatech.store.exception.ResourceNotFoundException;
import com.novatech.store.repository.ProductoRepository;
import java.util.List;
import org.springframework.stereotype.Service;

// Logica de negocio para los productos.
@Service
public class ProductoService {

    private final ProductoRepository repository;
    private final ListaPrecioService listaPrecioService;

    public ProductoService(ProductoRepository repository, ListaPrecioService listaPrecioService) {
        this.repository = repository;
        this.listaPrecioService = listaPrecioService;
    }

    // Trae todos los productos.
    public List<Producto> listar() {
        return repository.findAll();
    }

    // Trae un producto por su id. Si no existe, lanza error 404.
    public Producto obtener(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + id));
    }

    // Busca productos por una parte del nombre.
    public List<Producto> buscarPorNombre(String nombre) {
        return repository.findByNombreContainingIgnoreCase(nombre);
    }

    // Trae todos los productos de una categoria.
    public List<Producto> porCategoria(Integer idCategoria) {
        return repository.findByCategoriaIdCategoria(idCategoria);
    }

    // Crea un producto nuevo (id en null para que lo genere la base).
    public Producto crear(Producto producto) {
        producto.setIdProducto(null);
        aplicarDefaults(producto);
        return repository.save(producto);
    }

    // Actualiza un producto existente: lo busca, le cambia los datos y lo guarda.
    public Producto actualizar(Integer id, Producto datos) {
        Producto producto = obtener(id);
        producto.setNombre(datos.getNombre());
        producto.setDescripcion(datos.getDescripcion());
        producto.setPrecio(datos.getPrecio());
        producto.setPrecioLista(datos.getPrecioLista());
        producto.setStock(datos.getStock());
        producto.setStockMinimo(datos.getStockMinimo());
        producto.setCategoria(datos.getCategoria());
        producto.setProveedor(datos.getProveedor());
        producto.setImagen(datos.getImagen());
        aplicarDefaults(producto);
        return repository.save(producto);
    }

    public List<Producto> listarStockBajo() {
        return repository.findAll().stream()
                .filter(p -> {
                    int min = p.getStockMinimo() != null && p.getStockMinimo() > 0
                            ? p.getStockMinimo() : 5;
                    int stock = p.getStock() != null ? p.getStock() : 0;
                    return stock <= min;
                })
                .toList();
    }

    public List<Producto> listarConPrecioLista(String codigoLista, String canal, String tipoCliente) {
        String codigo = codigoLista;
        if (codigo == null || codigo.isBlank()) {
            codigo = listaPrecioService.resolverCodigoLista(canal, tipoCliente);
        }
        final String lista = codigo;
        return listar().stream().map(p -> enriquecerPrecioCanal(p, lista)).toList();
    }

    public Producto obtenerConPrecioLista(Integer id, String codigoLista, String canal, String tipoCliente) {
        Producto p = obtener(id);
        String codigo = codigoLista;
        if (codigo == null || codigo.isBlank()) {
            codigo = listaPrecioService.resolverCodigoLista(canal, tipoCliente);
        }
        return enriquecerPrecioCanal(p, codigo);
    }

    private Producto enriquecerPrecioCanal(Producto producto, String codigoLista) {
        var res = listaPrecioService.resolverPrecio(producto, codigoLista);
        producto.setPrecioCanal(res.getPrecioEfectivo());
        producto.setListaPrecioCodigo(res.getCodigoLista());
        return producto;
    }

    private void aplicarDefaults(Producto producto) {
        if (producto.getStockMinimo() == null || producto.getStockMinimo() < 0) {
            producto.setStockMinimo(5);
        }
        if (producto.getPrecioLista() == null && producto.getPrecio() != null) {
            producto.setPrecioLista(producto.getPrecio());
        }
    }

    // Borra un producto por su id.
    public void eliminar(Integer id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Producto no encontrado: " + id);
        }
        repository.deleteById(id);
    }
}
