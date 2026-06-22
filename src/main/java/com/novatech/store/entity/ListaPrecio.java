package com.novatech.store.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ListaPrecio")
public class ListaPrecio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lista_precio")
    private Integer idListaPrecio;

    @NotBlank
    @Column(name = "codigo", nullable = false, unique = true, length = 32)
    private String codigo;

    @NotBlank
    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    /** Descuento global % sobre precio base del producto (precio lista o precio catálogo). */
    @NotNull
    @Column(name = "descuento_global", precision = 5, scale = 2, nullable = false)
    private BigDecimal descuentoGlobal = BigDecimal.ZERO;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @OneToMany(mappedBy = "listaPrecio", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<ListaPrecioDetalle> detalles = new ArrayList<>();

    public Integer getIdListaPrecio() {
        return idListaPrecio;
    }

    public void setIdListaPrecio(Integer idListaPrecio) {
        this.idListaPrecio = idListaPrecio;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
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

    public BigDecimal getDescuentoGlobal() {
        return descuentoGlobal;
    }

    public void setDescuentoGlobal(BigDecimal descuentoGlobal) {
        this.descuentoGlobal = descuentoGlobal;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public List<ListaPrecioDetalle> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<ListaPrecioDetalle> detalles) {
        this.detalles = detalles;
    }
}
