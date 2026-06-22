package com.novatech.store.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "Emisor")
public class Emisor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_emisor")
    private Integer idEmisor;

    @Column(name = "razon_social")
    private String razonSocial;

    @Column(name = "cuit")
    private String cuit;

    @Column(name = "condicion_iva")
    private String condicionIva;

    @Column(name = "iibb")
    private String iibb;

    @Column(name = "domicilio")
    private String domicilio;

    @Column(name = "punto_venta")
    private Integer puntoVenta;

    /** HOMOLOGACION | PRODUCCION */
    @Column(name = "ambiente")
    private String ambiente;

    @Column(name = "es_default")
    private Boolean esDefault;

    @Column(name = "certificado_nombre")
    private String certificadoNombre;

    @Column(name = "certificado_vencimiento")
    private LocalDate certificadoVencimiento;

    public Emisor() {
    }

    public Integer getIdEmisor() {
        return idEmisor;
    }

    public void setIdEmisor(Integer idEmisor) {
        this.idEmisor = idEmisor;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getCuit() {
        return cuit;
    }

    public void setCuit(String cuit) {
        this.cuit = cuit;
    }

    public String getCondicionIva() {
        return condicionIva;
    }

    public void setCondicionIva(String condicionIva) {
        this.condicionIva = condicionIva;
    }

    public String getIibb() {
        return iibb;
    }

    public void setIibb(String iibb) {
        this.iibb = iibb;
    }

    public String getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio;
    }

    public Integer getPuntoVenta() {
        return puntoVenta;
    }

    public void setPuntoVenta(Integer puntoVenta) {
        this.puntoVenta = puntoVenta;
    }

    public String getAmbiente() {
        return ambiente;
    }

    public void setAmbiente(String ambiente) {
        this.ambiente = ambiente;
    }

    public Boolean getEsDefault() {
        return esDefault;
    }

    public void setEsDefault(Boolean esDefault) {
        this.esDefault = esDefault;
    }

    public String getCertificadoNombre() {
        return certificadoNombre;
    }

    public void setCertificadoNombre(String certificadoNombre) {
        this.certificadoNombre = certificadoNombre;
    }

    public LocalDate getCertificadoVencimiento() {
        return certificadoVencimiento;
    }

    public void setCertificadoVencimiento(LocalDate certificadoVencimiento) {
        this.certificadoVencimiento = certificadoVencimiento;
    }
}
