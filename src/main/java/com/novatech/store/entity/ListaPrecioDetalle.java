package com.novatech.store.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "ListaPrecioDetalle", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"id_lista_precio", "id_producto"})
})
public class ListaPrecioDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    private Integer idDetalle;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_lista_precio", nullable = false)
    @JsonIgnore
    private ListaPrecio listaPrecio;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_producto", nullable = false)
    @JsonIgnoreProperties({"imagen", "descripcion"})
    private Producto producto;

    /** Descuento % unitario (reemplaza el global para este producto). */
    @Column(name = "descuento_porcentaje", precision = 5, scale = 2)
    private BigDecimal descuentoPorcentaje;

    /** Precio fijo unitario (prioridad sobre descuentos). */
    @Column(name = "precio_fijo", precision = 12, scale = 2)
    private BigDecimal precioFijo;

    public Integer getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(Integer idDetalle) {
        this.idDetalle = idDetalle;
    }

    public ListaPrecio getListaPrecio() {
        return listaPrecio;
    }

    public void setListaPrecio(ListaPrecio listaPrecio) {
        this.listaPrecio = listaPrecio;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public BigDecimal getDescuentoPorcentaje() {
        return descuentoPorcentaje;
    }

    public void setDescuentoPorcentaje(BigDecimal descuentoPorcentaje) {
        this.descuentoPorcentaje = descuentoPorcentaje;
    }

    public BigDecimal getPrecioFijo() {
        return precioFijo;
    }

    public void setPrecioFijo(BigDecimal precioFijo) {
        this.precioFijo = precioFijo;
    }
}
