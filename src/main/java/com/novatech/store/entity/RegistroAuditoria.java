package com.novatech.store.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "RegistroAuditoria")
/**
 * Entidad JPA `RegistroAuditoria`: tabla y relaciones ORM; se serializa a JSON en respuestas API.
 */
public class RegistroAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_registro")
    private Integer idRegistro;

    @Column(name = "fecha")
    private LocalDateTime fecha;

    @Column(name = "usuario_nombre")
    private String usuarioNombre;

    @Column(name = "modulo")
    private String modulo;

    @Column(name = "accion")
    private String accion;

    @Column(name = "detalle", columnDefinition = "TEXT")
    private String detalle;

    @Column(name = "entidad")
    private String entidad;

    @Column(name = "entidad_id")
    private String entidadId;

    @Column(name = "ip")
    private String ip;

    @Column(name = "datos_antes", columnDefinition = "TEXT")
    private String datosAntes;

    @Column(name = "datos_despues", columnDefinition = "TEXT")
    private String datosDespues;

    public RegistroAuditoria() {
    }

    public Integer getIdRegistro() {
        return idRegistro;
    }

    public void setIdRegistro(Integer idRegistro) {
        this.idRegistro = idRegistro;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getUsuarioNombre() {
        return usuarioNombre;
    }

    public void setUsuarioNombre(String usuarioNombre) {
        this.usuarioNombre = usuarioNombre;
    }

    public String getModulo() {
        return modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public String getEntidad() {
        return entidad;
    }

    public void setEntidad(String entidad) {
        this.entidad = entidad;
    }

    public String getEntidadId() {
        return entidadId;
    }

    public void setEntidadId(String entidadId) {
        this.entidadId = entidadId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getDatosAntes() {
        return datosAntes;
    }

    public void setDatosAntes(String datosAntes) {
        this.datosAntes = datosAntes;
    }

    public String getDatosDespues() {
        return datosDespues;
    }

    public void setDatosDespues(String datosDespues) {
        this.datosDespues = datosDespues;
    }
}
