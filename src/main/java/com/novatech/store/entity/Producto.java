package com.novatech.store.entity;

import jakarta.persistence.*;
// BigDecimal se usa para guardar plata. Es mejor que double porque no pierde decimales.
import java.math.BigDecimal;

// Esta clase representa la tabla "Producto".
@Entity
@Table(name = "Producto")
public class Producto {

    // Identificador unico del producto.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Integer idProducto;

    // Nombre del producto.
    @Column(name = "nombre")
    private String nombre;

    // Descripcion larga del producto.
    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    // Precio del producto. precision = 12 y scale = 2 significa hasta 12 numeros en total
    // y 2 despues de la coma (por ejemplo 1234567890.99).
    @Column(name = "precio", precision = 12, scale = 2)
    private BigDecimal precio;

    // Cantidad de unidades que tenemos en stock.
    @Column(name = "stock")
    private Integer stock;

    // Un producto pertenece a UNA categoria, pero una categoria puede tener MUCHOS productos.
    // Eso se llama relacion "muchos a uno" (ManyToOne).
    // @JoinColumn dice cual es la columna que guarda el id de la categoria dentro de la tabla Producto.
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_categoria")
    private Categoria categoria;

    // Nombre del proveedor que nos vende este producto.
    @Column(name = "proveedor")
    private String proveedor;

    // Foto del producto guardada como texto en formato "base64" (un data URL,
    // por ejemplo: data:image/png;base64,AAAA...). Lo mandamos asi desde el
    // frontend para no tener que manejar archivos sueltos en el servidor.
    // columnDefinition = "LONGTEXT" -> permite guardar textos muy largos (la imagen pesa).
    @Column(name = "imagen", columnDefinition = "LONGTEXT")
    private String imagen;

    // Constructor vacio para JPA.
    public Producto() {
    }

    // Getters y setters.

    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
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

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public String getProveedor() {
        return proveedor;
    }

    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
}
