package com.novatech.store.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// Esta clase representa la tabla "Carrito".
// Es el carrito de compras de un usuario.
@Entity
@Table(name = "Carrito")
/**
 * Entidad JPA `Carrito`: tabla y relaciones ORM; se serializa a JSON en respuestas API.
 */
public class Carrito {

    // Identificador unico del carrito.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_carrito")
    private Integer idCarrito;

    // El carrito pertenece a un usuario.
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    // Fecha y hora en que se creo el carrito.
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    // Constructor vacio para JPA.
    public Carrito() {
    }

    // Getters y setters.

    public Integer getIdCarrito() {
        return idCarrito;
    }

    public void setIdCarrito(Integer idCarrito) {
        this.idCarrito = idCarrito;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
