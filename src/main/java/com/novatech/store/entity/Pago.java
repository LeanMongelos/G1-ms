package com.novatech.store.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

// Esta clase representa la tabla "Pago".
// Guarda la informacion de un pago hecho para un pedido.
@Entity
@Table(name = "Pago")
public class Pago {

    // Identificador unico del pago.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago")
    private Integer idPago;

    // A que pedido corresponde este pago.
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_pedido")
    private Pedido pedido;

    // Fecha y hora en que se hizo el pago.
    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;

    // Monto que se pago.
    @Column(name = "monto", precision = 12, scale = 2)
    private BigDecimal monto;

    // Metodo de pago, por ejemplo "TARJETA", "EFECTIVO", "TRANSFERENCIA".
    @Column(name = "metodo")
    private String metodo;

    // Que usuario aprobo el pago (por ejemplo un administrador).
    // Apunta a la tabla Usuario a traves de la columna "aprobado_por".
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "aprobado_por")
    private Usuario aprobadoPor;

    // Constructor vacio para JPA.
    public Pago() {
    }

    // Getters y setters.

    public Integer getIdPago() {
        return idPago;
    }

    public void setIdPago(Integer idPago) {
        this.idPago = idPago;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public LocalDateTime getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDateTime fechaPago) {
        this.fechaPago = fechaPago;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public String getMetodo() {
        return metodo;
    }

    public void setMetodo(String metodo) {
        this.metodo = metodo;
    }

    public Usuario getAprobadoPor() {
        return aprobadoPor;
    }

    public void setAprobadoPor(Usuario aprobadoPor) {
        this.aprobadoPor = aprobadoPor;
    }
}
