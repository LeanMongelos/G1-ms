package com.novatech.store.entity;

// Importamos las anotaciones de JPA para convertir esta clase en una tabla.
import jakarta.persistence.*;
// Anotacion de Bean Validation (Jakarta) para validar los datos de entrada.
import jakarta.validation.constraints.NotBlank;

// @Entity = esta clase es una tabla de la base de datos.
@Entity
// La tabla se va a llamar "Categoria".
@Table(name = "Categoria")
public class Categoria {

    // Clave primaria: identifica de forma unica a cada categoria.
    @Id
    // El id lo genera MySQL automaticamente.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    private Integer idCategoria;

    // Nombre de la categoria, por ejemplo "Notebooks". Es obligatorio y no puede ir vacio.
    @NotBlank(message = "El nombre de la categoria es obligatorio.")
    @Column(name = "nombre")
    private String nombre;

    // Descripcion mas larga de la categoria.
    // columnDefinition = "TEXT" sirve para poder guardar textos largos.
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
