package com.novatech.store.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "ConfiguracionSistema")
public class ConfiguracionSistema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_config")
    private Integer idConfig;

    @Column(name = "grupo")
    private String grupo;

    @Column(name = "clave", unique = true)
    private String clave;

    @Column(name = "valor", columnDefinition = "TEXT")
    private String valor;

    @Column(name = "descripcion")
    private String descripcion;

    public ConfiguracionSistema() {
    }

    public Integer getIdConfig() {
        return idConfig;
    }

    public void setIdConfig(Integer idConfig) {
        this.idConfig = idConfig;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
