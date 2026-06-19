package com.novatech.store.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// Esta clase representa la tabla "Resena" (una resena/opinion de un producto).
// En el DER aparece como "Resena"; le sacamos la enie para evitar problemas de codificacion.
@Entity
@Table(name = "Resena")
public class Resena {

    // Identificador unico de la resena.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_resena")
    private Integer idResena;

    // Sobre que producto es la resena.
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_producto")
    private Producto producto;

    // Que usuario escribio la resena.
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    // Comentario que escribio el usuario.
    @Column(name = "comentario", columnDefinition = "TEXT")
    private String comentario;

    // Puntuacion del 1 al 5 (las estrellitas).
    @Column(name = "puntuacion")
    private Integer puntuacion;

    // Fecha y hora en que se escribio la resena.
    @Column(name = "fecha")
    private LocalDateTime fecha;

    // Constructor vacio para JPA.
    public Resena() {
    }

    // Getters y setters.

    public Integer getIdResena() {
        return idResena;
    }

    public void setIdResena(Integer idResena) {
        this.idResena = idResena;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public Integer getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(Integer puntuacion) {
        this.puntuacion = puntuacion;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
}
