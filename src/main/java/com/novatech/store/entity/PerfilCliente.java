package com.novatech.store.entity;

import jakarta.persistence.*;

// Esta clase representa la tabla "PerfilCliente".
// Guarda los datos extra de un usuario que ademas es cliente (direccion, telefono, etc).
@Entity
@Table(name = "PerfilCliente")
public class PerfilCliente {

    // Identificador unico del perfil del cliente.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Integer idCliente;

    // Cada perfil de cliente esta conectado a un usuario.
    // Relacion muchos a uno: muchos perfiles podrian apuntar a un usuario (en este caso normalmente uno).
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    // Direccion del cliente.
    @Column(name = "direccion")
    private String direccion;

    // Telefono de contacto.
    @Column(name = "telefono")
    private String telefono;

    // Numero que representa el historial de credito del cliente.
    @Column(name = "historial_crediticio")
    private Integer historialCrediticio;

    // Tipo de cliente, por ejemplo "MINORISTA" o "MAYORISTA".
    @Column(name = "tipo_cliente")
    private String tipoCliente;

    // Constructor vacio para JPA.
    public PerfilCliente() {
    }

    // Getters y setters.

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Integer getHistorialCrediticio() {
        return historialCrediticio;
    }

    public void setHistorialCrediticio(Integer historialCrediticio) {
        this.historialCrediticio = historialCrediticio;
    }

    public String getTipoCliente() {
        return tipoCliente;
    }

    public void setTipoCliente(String tipoCliente) {
        this.tipoCliente = tipoCliente;
    }
}
