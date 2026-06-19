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

    // El constructor recibe el repositorio que Spring crea por nosotros.
    public ProductoService(ProductoRepository repository) {
        this.repository = repository;
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
        return repository.save(producto);
    }

    // Actualiza un producto existente: lo busca, le cambia los datos y lo guarda.
    public Producto actualizar(Integer id, Producto datos) {
        Producto producto = obtener(id);
        producto.setNombre(datos.getNombre());
        producto.setDescripcion(datos.getDescripcion());
        producto.setPrecio(datos.getPrecio());
        producto.setStock(datos.getStock());
        producto.setCategoria(datos.getCategoria());
        producto.setProveedor(datos.getProveedor());
        producto.setImagen(datos.getImagen());
        return repository.save(producto);
    }

    // Borra un producto por su id.
    public void eliminar(Integer id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Producto no encontrado: " + id);
        }
        repository.deleteById(id);
    }
}
