package com.novatech.store.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "SolicitudDevolucion")
/**
 * Entidad JPA `SolicitudDevolucion`: tabla y relaciones ORM; se serializa a JSON en respuestas API.
 */
public class SolicitudDevolucion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_solicitud")
    private Integer idSolicitud;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_pedido")
    @JsonIgnoreProperties({"usuario", "notas"})
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_cliente")
    @JsonIgnoreProperties({"notas", "sitioWeb", "lat", "lng", "historialCrediticio", "usuario"})
    private PerfilCliente cliente;

    @Column(name = "motivo")
    private String motivo;

    /** SOLICITADA, APROBADA, RECHAZADA, EN_TRANSITO, RECIBIDA, REEMBOLSADA */
    @Column(name = "estado")
    private String estado;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "lineas_json", columnDefinition = "TEXT")
    private String lineasJson;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    public SolicitudDevolucion() {
    }

    public Integer getIdSolicitud() {
        return idSolicitud;
    }

    public void setIdSolicitud(Integer idSolicitud) {
        this.idSolicitud = idSolicitud;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public PerfilCliente getCliente() {
        return cliente;
    }

    public void setCliente(PerfilCliente cliente) {
        this.cliente = cliente;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getLineasJson() {
        return lineasJson;
    }

    public void setLineasJson(String lineasJson) {
        this.lineasJson = lineasJson;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
}
