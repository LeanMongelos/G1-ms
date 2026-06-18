package com.novatech.store.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

// Esta clase representa la tabla "DetallePedido".
// Es cada renglon de un pedido: que producto, que cantidad y a que precio se compro.
@Entity
@Table(name = "DetallePedido")
public class DetallePedido {

    // Identificador unico del renglon del pedido.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    private Integer idDetalle;

    // A que pedido pertenece este renglon.
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_pedido")
    private Pedido pedido;

    // Que producto se compro en este renglon.
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_producto")
    private Producto producto;

    // Cantidad de unidades compradas.
    @Column(name = "cantidad")
    private Integer cantidad;

    // Precio de cada unidad en el momento de la compra.
    // Lo guardamos aparte porque el precio del producto puede cambiar con el tiempo.
    @Column(name = "precio_unitario", precision = 12, scale = 2)
    private BigDecimal precioUnitario;

    // Constructor vacio para JPA.
    public DetallePedido() {
    }

    // Getters y setters.

    public Integer getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(Integer idDetalle) {
        this.idDetalle = idDetalle;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
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

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }
}
