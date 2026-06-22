package com.novatech.store.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "PlantillaImpresion")
public class PlantillaImpresion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_plantilla")
    private Integer idPlantilla;

    /** FACTURA, PRESUPUESTO, REMITO, etc. */
    @Column(name = "tipo")
    private String tipo;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "contenido_json", columnDefinition = "TEXT")
    private String contenidoJson;

    @Column(name = "es_default")
    private Boolean esDefault;

    @Column(name = "activo")
    private Boolean activo;

    public PlantillaImpresion() {
    }

    public Integer getIdPlantilla() {
        return idPlantilla;
    }

    public void setIdPlantilla(Integer idPlantilla) {
        this.idPlantilla = idPlantilla;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getContenidoJson() {
        return contenidoJson;
    }

    public void setContenidoJson(String contenidoJson) {
        this.contenidoJson = contenidoJson;
    }

    public Boolean getEsDefault() {
        return esDefault;
    }

    public void setEsDefault(Boolean esDefault) {
        this.esDefault = esDefault;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}
