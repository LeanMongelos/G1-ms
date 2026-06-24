package com.novatech.store.service;

import com.novatech.store.entity.Categoria;
import com.novatech.store.exception.ReglaNegocioException;
import com.novatech.store.exception.ResourceNotFoundException;
import com.novatech.store.repository.CategoriaRepository;
import com.novatech.store.validation.NombreCategoriaValidator;
import java.util.List;
import org.springframework.stereotype.Service;

// @Service le dice a Spring que esta clase tiene la "logica de negocio".
// La logica de negocio son las reglas/pasos de la aplicacion.
// El service usa el repository para hablar con la base de datos.
/**
 * Servicio `CategoriaService`: reglas de negocio, transacciones y orquestación de Categoria. Los controllers delegan aquí; no accede HTTP directamente.
 */
@Service
public class CategoriaService {

    // Guardamos el repositorio en una variable para poder usarlo.
    private final CategoriaRepository repository;

    // Constructor: Spring nos pasa el repositorio solo (esto se llama inyeccion de dependencias).
    public CategoriaService(CategoriaRepository repository) {
        this.repository = repository;
    }

    // Devuelve la lista de todas las categorias.
    public List<Categoria> listar() {
        return repository.findAll();
    }

    // Busca una categoria por su id.
    // Si no la encuentra, lanzamos un error que despues se transforma en un 404 (no encontrado).
    public Categoria obtener(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria no encontrada: " + id));
    }

    // Crea una categoria nueva.
    // Ponemos el id en null para asegurarnos de que MySQL genere uno nuevo.
    public Categoria crear(Categoria categoria) {
        normalizar(categoria);
        validarNombreUnico(categoria.getNombre(), null);
        categoria.setIdCategoria(null);
        return repository.save(categoria);
    }

    // Actualiza una categoria que ya existe.
    // Primero la buscamos, despues le cambiamos los datos y la guardamos.
    public Categoria actualizar(Integer id, Categoria datos) {
        Categoria categoria = obtener(id);
        categoria.setNombre(datos.getNombre());
        categoria.setDescripcion(datos.getDescripcion());
        normalizar(categoria);
        validarNombreUnico(categoria.getNombre(), id);
        return repository.save(categoria);
    }

    // Borra una categoria por su id.
    // Si no existe, avisamos con un error.
    public void eliminar(Integer id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Categoria no encontrada: " + id);
        }
        repository.deleteById(id);
    }

    // Recorta espacios del nombre y descripcion antes de guardar.
    private void normalizar(Categoria categoria) {
        if (categoria.getNombre() != null) {
            categoria.setNombre(categoria.getNombre().trim());
        }
        if (categoria.getDescripcion() != null) {
            categoria.setDescripcion(categoria.getDescripcion().trim());
        }
        // Reglas de nombre coherente (bloquea basura tipo "4dfs---0.0.00.df0sdf").
        NombreCategoriaValidator.validar(categoria.getNombre());
    }

    // Evita dos categorias con el mismo nombre (sin importar mayusculas).
    private void validarNombreUnico(String nombre, Integer idExcluir) {
        repository.findByNombreIgnoreCase(nombre).ifPresent(existente -> {
            if (idExcluir == null || !existente.getIdCategoria().equals(idExcluir)) {
                throw new ReglaNegocioException("Ya existe una categoria con ese nombre.");
            }
        });
    }
}
