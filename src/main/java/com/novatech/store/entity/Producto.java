package com.novatech.store.entity;

import jakarta.persistence.*;
// Anotaciones de Bean Validation (Jakarta) para validar los datos de entrada.
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
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

    // Nombre del producto. Es obligatorio y no puede ir vacio.
    @NotBlank(message = "El nombre del producto es obligatorio.")
    @Column(name = "nombre")
    private String nombre;

    // Descripcion larga del producto.
    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    // Precio del producto. precision = 12 y scale = 2 significa hasta 12 numeros en total
    // y 2 despues de la coma (por ejemplo 1234567890.99).
    // Es obligatorio y no puede ser negativo.
    @NotNull(message = "El precio es obligatorio.")
    @PositiveOrZero(message = "El precio no puede ser negativo.")
    @Column(name = "precio", precision = 12, scale = 2)
    private BigDecimal precio;

    /** Precio de lista / PVP de referencia (puede ser mayor al precio de venta). */
    @Column(name = "precio_lista", precision = 12, scale = 2)
    private BigDecimal precioLista;

    // Cantidad de unidades que tenemos en stock.
    // Es obligatorio y no puede ser negativo.
    @NotNull(message = "El stock es obligatorio.")
    @PositiveOrZero(message = "El stock no puede ser negativo.")
    @Column(name = "stock")
    private Integer stock;

    /** Umbral mínimo de stock; por debajo se alerta y se sugiere reposición. */
    @Column(name = "stock_minimo")
    private Integer stockMinimo;

    // Un producto pertenece a UNA categoria
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

    /** Precio resuelto según lista/canal (no persistido). */
    @Transient
    private BigDecimal precioCanal;

    @Transient
    private String listaPrecioCodigo;

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

    public BigDecimal getPrecioLista() {
        return precioLista;
    }

    public void setPrecioLista(BigDecimal precioLista) {
        this.precioLista = precioLista;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(Integer stockMinimo) {
        this.stockMinimo = stockMinimo;
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

    public BigDecimal getPrecioCanal() {
        return precioCanal;
    }

    public void setPrecioCanal(BigDecimal precioCanal) {
        this.precioCanal = precioCanal;
    }

    public String getListaPrecioCodigo() {
        return listaPrecioCodigo;
    }

    public void setListaPrecioCodigo(String listaPrecioCodigo) {
        this.listaPrecioCodigo = listaPrecioCodigo;
    }
}
