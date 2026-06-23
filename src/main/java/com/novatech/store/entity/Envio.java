package com.novatech.store.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDateTime;

// Esta clase representa la tabla "Envio".
// Guarda los datos del envio de un pedido.
@Entity
@Table(name = "Envio")
public class Envio {

    // Identificador unico del envio.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_envio")
    private Integer idEnvio;

    // A que pedido corresponde este envio.
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_pedido")
    @JsonIgnoreProperties({"notas"})
    private Pedido pedido;

    // Direccion a donde se manda el pedido. Es obligatoria y no puede ir vacia.
    @NotBlank(message = "La direccion de envio es obligatoria.")
    @Column(name = "direccion_envio")
    private String direccionEnvio;

    // Empresa que hace el envio, por ejemplo "Correo Argentino", "Andreani".
    @Column(name = "empresa_logistica")
    private String empresaLogistica;

    // Estado del envio, por ejemplo "PREPARANDO", "EN_CAMINO", "ENTREGADO".
    @Column(name = "estado_envio")
    private String estadoEnvio;

    @Column(name = "numero_tracking")
    private String numeroTracking;

    @Column(name = "fecha_despacho")
    private LocalDateTime fechaDespacho;

    @Column(name = "costo_envio", precision = 12, scale = 2)
    private BigDecimal costoEnvio;

    // Constructor vacio para JPA.
    public Envio() {
    }

    // Getters y setters.

    public Integer getIdEnvio() {
        return idEnvio;
    }

    public void setIdEnvio(Integer idEnvio) {
        this.idEnvio = idEnvio;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public String getDireccionEnvio() {
        return direccionEnvio;
    }

    public void setDireccionEnvio(String direccionEnvio) {
        this.direccionEnvio = direccionEnvio;
    }

    public String getEmpresaLogistica() {
        return empresaLogistica;
    }

    public void setEmpresaLogistica(String empresaLogistica) {
        this.empresaLogistica = empresaLogistica;
    }

    public String getEstadoEnvio() {
        return estadoEnvio;
    }

    public void setEstadoEnvio(String estadoEnvio) {
        this.estadoEnvio = estadoEnvio;
    }

    public String getNumeroTracking() {
        return numeroTracking;
    }

    public void setNumeroTracking(String numeroTracking) {
        this.numeroTracking = numeroTracking;
    }

    public LocalDateTime getFechaDespacho() {
        return fechaDespacho;
    }

    public void setFechaDespacho(LocalDateTime fechaDespacho) {
        this.fechaDespacho = fechaDespacho;
    }

    public BigDecimal getCostoEnvio() {
        return costoEnvio;
    }

    public void setCostoEnvio(BigDecimal costoEnvio) {
        this.costoEnvio = costoEnvio;
    }
}
