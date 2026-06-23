package com.novatech.store.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Presupuesto")
public class Presupuesto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_presupuesto")
    private Integer idPresupuesto;

    @Column(name = "numero_presupuesto", unique = true)
    private String numeroPresupuesto;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_cliente")
    @JsonIgnoreProperties({"notas", "sitioWeb", "lat", "lng", "historialCrediticio"})
    private PerfilCliente cliente;

    @Column(name = "fecha")
    private LocalDateTime fecha;

    @Column(name = "validez_hasta")
    private LocalDate validezHasta;

    /** BORRADOR, ENVIADO, APROBADO, VENCIDO, FACTURADO */
    @Column(name = "estado")
    private String estado;

    @Column(name = "subtotal", precision = 12, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "iva", precision = 12, scale = 2)
    private BigDecimal iva;

    @Column(name = "total", precision = 12, scale = 2)
    private BigDecimal total;

    @Column(name = "notas", columnDefinition = "TEXT")
    private String notas;

    @OneToMany(mappedBy = "presupuesto", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<DetallePresupuesto> lineas = new ArrayList<>();

    public Presupuesto() {
    }

    public Integer getIdPresupuesto() {
        return idPresupuesto;
    }

    public void setIdPresupuesto(Integer idPresupuesto) {
        this.idPresupuesto = idPresupuesto;
    }

    public String getNumeroPresupuesto() {
        return numeroPresupuesto;
    }

    public void setNumeroPresupuesto(String numeroPresupuesto) {
        this.numeroPresupuesto = numeroPresupuesto;
    }

    public PerfilCliente getCliente() {
        return cliente;
    }

    public void setCliente(PerfilCliente cliente) {
        this.cliente = cliente;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public LocalDate getValidezHasta() {
        return validezHasta;
    }

    public void setValidezHasta(LocalDate validezHasta) {
        this.validezHasta = validezHasta;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getIva() {
        return iva;
    }

    public void setIva(BigDecimal iva) {
        this.iva = iva;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public List<DetallePresupuesto> getLineas() {
        return lineas;
    }

    public void setLineas(List<DetallePresupuesto> lineas) {
        this.lineas = lineas;
    }
}
