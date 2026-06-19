package com.novatech.store.entity;

import jakarta.persistence.*;

// Esta clase representa la tabla "DetalleCarrito".
// Es cada renglon de un carrito: que producto y que cantidad se agrego.
@Entity
@Table(name = "DetalleCarrito")
public class DetalleCarrito {

    // Identificador unico del renglon del carrito.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_carrito")
    private Integer idDetalleCarrito;

    // A que carrito pertenece este renglon.
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_carrito")
    private Carrito carrito;

    // Que producto se agrego en este renglon.
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_producto")
    private Producto producto;

    // Cuantas unidades de ese producto.
    @Column(name = "cantidad")
    private Integer cantidad;

    // Constructor vacio para JPA.
    public DetalleCarrito() {
    }

    // Getters y setters.

    public Integer getIdDetalleCarrito() {
        return idDetalleCarrito;
    }

    public void setIdDetalleCarrito(Integer idDetalleCarrito) {
        this.idDetalleCarrito = idDetalleCarrito;
    }

    public Carrito getCarrito() {
        return carrito;
    }

    public void setCarrito(Carrito carrito) {
        this.carrito = carrito;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
}
