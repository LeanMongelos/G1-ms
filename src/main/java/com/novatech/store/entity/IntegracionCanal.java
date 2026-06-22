package com.novatech.store.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "IntegracionCanal")
public class IntegracionCanal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_integracion")
    private Integer idIntegracion;

    /** WHATSAPP, INSTAGRAM, FACEBOOK, EMAIL, N8N */
    @Column(name = "tipo", unique = true)
    private String tipo;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "activo")
    private Boolean activo;

    /** CONECTADO, DESCONECTADO, ERROR */
    @Column(name = "estado_conexion")
    private String estadoConexion;

    @Column(name = "config_json", columnDefinition = "TEXT")
    private String configJson;

    public IntegracionCanal() {
    }

    public Integer getIdIntegracion() {
        return idIntegracion;
    }

    public void setIdIntegracion(Integer idIntegracion) {
        this.idIntegracion = idIntegracion;
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

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public String getEstadoConexion() {
        return estadoConexion;
    }

    public void setEstadoConexion(String estadoConexion) {
        this.estadoConexion = estadoConexion;
    }

    public String getConfigJson() {
        return configJson;
    }

    public void setConfigJson(String configJson) {
        this.configJson = configJson;
    }
}
