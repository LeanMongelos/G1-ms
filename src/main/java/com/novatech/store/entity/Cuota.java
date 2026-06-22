package com.novatech.store.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "Cuota")
public class Cuota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cuota")
    private Integer idCuota;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_plan")
    private PlanCuotas plan;

    @Column(name = "numero_cuota")
    private Integer numeroCuota;

    @Column(name = "monto", precision = 12, scale = 2)
    private BigDecimal monto;

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;

    /** PENDIENTE, PAGADA, VENCIDA */
    @Column(name = "estado")
    private String estado;

    @Column(name = "aviso_vencimiento_enviado")
    private Boolean avisoVencimientoEnviado;

    public Cuota() {
    }

    public Integer getIdCuota() {
        return idCuota;
    }

    public void setIdCuota(Integer idCuota) {
        this.idCuota = idCuota;
    }

    public PlanCuotas getPlan() {
        return plan;
    }

    public void setPlan(PlanCuotas plan) {
        this.plan = plan;
    }

    public Integer getNumeroCuota() {
        return numeroCuota;
    }

    public void setNumeroCuota(Integer numeroCuota) {
        this.numeroCuota = numeroCuota;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public LocalDateTime getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDateTime fechaPago) {
        this.fechaPago = fechaPago;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Boolean getAvisoVencimientoEnviado() {
        return avisoVencimientoEnviado;
    }

    public void setAvisoVencimientoEnviado(Boolean avisoVencimientoEnviado) {
        this.avisoVencimientoEnviado = avisoVencimientoEnviado;
    }
}
