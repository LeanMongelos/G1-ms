package com.novatech.store.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

// Esta clase representa la tabla "Pago".
// Guarda la informacion de un pago hecho para un pedido.
@Entity
@Table(name = "Pago")
/**
 * Entidad JPA `Pago`: tabla y relaciones ORM; se serializa a JSON en respuestas API.
 */
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

    // Metodo de pago. Valores posibles:
    // "TARJETA", "EFECTIVO", "TRANSFERENCIA", "MERCADO_PAGO",
    // "BILLETERA_VIRTUAL", "QR", "PRESTAMO_CASA".
    @Column(name = "metodo")
    private String metodo;

    // Nombre de la billetera virtual elegida (ej: "MODO", "Uala", "Naranja X").
    // Solo se usa cuando el metodo es "BILLETERA_VIRTUAL". Es opcional.
    @Column(name = "proveedor_billetera")
    private String proveedorBilletera;

    // Numero/id de la transaccion (simulado). Sirve para identificar el pago
    // en Mercado Pago, QR, billetera, etc. Es opcional.
    @Column(name = "referencia")
    private String referencia;

    // Estado del pago, por ejemplo "APROBADO" o "PENDIENTE". Es opcional.
    @Column(name = "estado")
    private String estado;

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

    public String getProveedorBilletera() {
        return proveedorBilletera;
    }

    public void setProveedorBilletera(String proveedorBilletera) {
        this.proveedorBilletera = proveedorBilletera;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Usuario getAprobadoPor() {
        return aprobadoPor;
    }

    public void setAprobadoPor(Usuario aprobadoPor) {
        this.aprobadoPor = aprobadoPor;
    }
}
