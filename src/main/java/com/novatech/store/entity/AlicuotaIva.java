package com.novatech.store.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "AlicuotaIva")
public class AlicuotaIva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_alicuota")
    private Integer idAlicuota;

    @Column(name = "codigo")
    private String codigo;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "porcentaje")
    private BigDecimal porcentaje;

    @Column(name = "activo")
    private Boolean activo;

    public AlicuotaIva() {
    }

    public Integer getIdAlicuota() {
        return idAlicuota;
    }

    public void setIdAlicuota(Integer idAlicuota) {
        this.idAlicuota = idAlicuota;
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

    public BigDecimal getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(BigDecimal porcentaje) {
        this.porcentaje = porcentaje;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}
