package com.novatech.store.entity;

import jakarta.persistence.*;
// Anotacion de Bean Validation (Jakarta) para validar los datos de entrada.
import jakarta.validation.constraints.NotBlank;

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
    private Pedido pedido;

    // Direccion a donde se manda el pedido. Es obligatoria y no puede ir vacia.
    @NotBlank(message = "La direccion de envio es obligatoria.")
    @Column(name = "direccion_envio")
    private String direccionEnvio;

    // Empresa que hace el envio, por ejemplo "Correo Argentino", "Andreani".
    @Column(name = "empresa_logistica")
    private String empresaLogistica;

    // Estado del envio, por ejemplo "PREPARANDO", "EN CAMINO", "ENTREGADO".
    @Column(name = "estado_envio")
    private String estadoEnvio;

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
}
