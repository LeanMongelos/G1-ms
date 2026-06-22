package com.novatech.store.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Campana")
public class Campana {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_campana")
    private Integer idCampana;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    /** PROMOCION, RECORDATORIO_PAGO, CUOTA_VENCIDA, NOVEDAD */
    @Column(name = "tipo")
    private String tipo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_promocion")
    private Promocion promocion;

    @Column(name = "asunto")
    private String asunto;

    @Column(name = "cuerpo_mensaje", columnDefinition = "TEXT")
    private String cuerpoMensaje;

    /** EMAIL, SMS, AMBOS */
    @Column(name = "canal")
    private String canal;

    /** BORRADOR, PROGRAMADA, ENVIADA, CANCELADA */
    @Column(name = "estado")
    private String estado;

    /** TODOS, CLIENTES_ACTIVOS, CON_DEUDA, MOROSOS, MINORISTA, MAYORISTA */
    @Column(name = "segmento")
    private String segmento;

    @Column(name = "fecha_programada")
    private LocalDateTime fechaProgramada;

    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;

    @Column(name = "cantidad_enviados")
    private Integer cantidadEnviados;

    public Campana() {
    }

    public Integer getIdCampana() {
        return idCampana;
    }

    public void setIdCampana(Integer idCampana) {
        this.idCampana = idCampana;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Promocion getPromocion() {
        return promocion;
    }

    public void setPromocion(Promocion promocion) {
        this.promocion = promocion;
    }

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public String getCuerpoMensaje() {
        return cuerpoMensaje;
    }

    public void setCuerpoMensaje(String cuerpoMensaje) {
        this.cuerpoMensaje = cuerpoMensaje;
    }

    public String getCanal() {
        return canal;
    }

    public void setCanal(String canal) {
        this.canal = canal;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getSegmento() {
        return segmento;
    }

    public void setSegmento(String segmento) {
        this.segmento = segmento;
    }

    public LocalDateTime getFechaProgramada() {
        return fechaProgramada;
    }

    public void setFechaProgramada(LocalDateTime fechaProgramada) {
        this.fechaProgramada = fechaProgramada;
    }

    public LocalDateTime getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(LocalDateTime fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

    public Integer getCantidadEnviados() {
        return cantidadEnviados;
    }

    public void setCantidadEnviados(Integer cantidadEnviados) {
        this.cantidadEnviados = cantidadEnviados;
    }
}
