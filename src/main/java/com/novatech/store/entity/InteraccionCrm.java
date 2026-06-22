package com.novatech.store.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "InteraccionCrm")
public class InteraccionCrm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_interaccion")
    private Integer idInteraccion;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_cliente")
    private PerfilCliente cliente;

    /** LLAMADA, EMAIL, WHATSAPP, NOTA, RECLAMO, SEGUIMIENTO */
    @Column(name = "tipo")
    private String tipo;

    @Column(name = "titulo")
    private String titulo;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "fecha")
    private LocalDateTime fecha;

    /** BAJA, MEDIA, ALTA */
    @Column(name = "prioridad")
    private String prioridad;

    /** ABIERTA, CERRADA */
    @Column(name = "estado")
    private String estado;

    public InteraccionCrm() {
    }

    public Integer getIdInteraccion() {
        return idInteraccion;
    }

    public void setIdInteraccion(Integer idInteraccion) {
        this.idInteraccion = idInteraccion;
    }

    public PerfilCliente getCliente() {
        return cliente;
    }

    public void setCliente(PerfilCliente cliente) {
        this.cliente = cliente;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
