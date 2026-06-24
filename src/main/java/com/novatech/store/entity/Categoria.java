package com.novatech.store.entity;

// Importamos las anotaciones de JPA para convertir esta clase en una tabla.
import jakarta.persistence.*;
// Anotacion de Bean Validation (Jakarta) para validar los datos de entrada.
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// @Entity = esta clase es una tabla de la base de datos.
@Entity
// La tabla se va a llamar "Categoria".
@Table(name = "Categoria")
/**
 * Entidad JPA `Categoria`: tabla y relaciones ORM; se serializa a JSON en respuestas API.
 */
public class Categoria {

    // Clave primaria: identifica de forma unica a cada categoria.
    @Id
    // El id lo genera MySQL automaticamente.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    private Integer idCategoria;

    // Nombre de la categoria, por ejemplo "Notebooks". Validacion detallada en NombreCategoriaValidator.
    @NotBlank(message = "El nombre de la categoria es obligatorio.")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres.")
    @Column(name = "nombre")
    private String nombre;

    // Descripcion mas larga de la categoria.
    // columnDefinition = "TEXT" sirve para poder guardar textos largos.
    @Size(max = 500, message = "La descripcion no puede superar los 500 caracteres.")
    @Pattern(
        regexp = "^$|^[\\p{L}\\p{N}\\p{Zs}.,;:!?()\\-']{3,500}$",
        message = "La descripcion tiene caracteres no permitidos."
    )
    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    // Constructor vacio que necesita JPA.
    public Categoria() {
    }

    // Getters y setters (leer y modificar los atributos).

    public Integer getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Integer idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
