package com.novatech.store.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Factura")
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_factura")
    private Integer idFactura;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_pedido")
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_presupuesto")
    private Presupuesto presupuesto;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_remito")
    private Remito remito;

    @Column(name = "numero_factura", unique = true)
    private String numeroFactura;

    @Column(name = "punto_venta")
    private Integer puntoVenta;

    @Column(name = "fecha_emision")
    private LocalDateTime fechaEmision;

    @Column(name = "subtotal", precision = 12, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "iva", precision = 12, scale = 2)
    private BigDecimal iva;

    @Column(name = "total", precision = 12, scale = 2)
    private BigDecimal total;

    /** BORRADOR, EMITIDA, ANULADA */
    @Column(name = "estado")
    private String estado;

    /** FACTURA_A, FACTURA_B, FACTURA_C */
    @Column(name = "tipo_comprobante")
    private String tipoComprobante;

    @Column(name = "cuit_cliente")
    private String cuitCliente;

    /** CONSUMIDOR_FINAL, RESPONSABLE_INSCRIPTO, etc. */
    @Column(name = "condicion_iva_cliente")
    private String condicionIvaCliente;

    @Column(name = "cae")
    private String cae;

    @Column(name = "cae_vencimiento")
    private LocalDate caeVencimiento;

    @Column(name = "notas", columnDefinition = "TEXT")
    private String notas;

    /** CONTADO, PRESTAMO_PERSONAL */
    @Column(name = "forma_cobro")
    private String formaCobro;

    @Column(name = "cantidad_cuotas")
    private Integer cantidadCuotas;

    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<DetalleFactura> lineas = new ArrayList<>();

    public Factura() {
    }

    public Integer getIdFactura() {
        return idFactura;
    }

    public void setIdFactura(Integer idFactura) {
        this.idFactura = idFactura;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public String getNumeroFactura() {
        return numeroFactura;
    }

    public void setNumeroFactura(String numeroFactura) {
        this.numeroFactura = numeroFactura;
    }

    public LocalDateTime getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDateTime fechaEmision) {
        this.fechaEmision = fechaEmision;
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getTipoComprobante() {
        return tipoComprobante;
    }

    public void setTipoComprobante(String tipoComprobante) {
        this.tipoComprobante = tipoComprobante;
    }

    public String getCuitCliente() {
        return cuitCliente;
    }

    public void setCuitCliente(String cuitCliente) {
        this.cuitCliente = cuitCliente;
    }

    public String getFormaCobro() {
        return formaCobro;
    }

    public void setFormaCobro(String formaCobro) {
        this.formaCobro = formaCobro;
    }

    public Integer getCantidadCuotas() {
        return cantidadCuotas;
    }

    public void setCantidadCuotas(Integer cantidadCuotas) {
        this.cantidadCuotas = cantidadCuotas;
    }

    public Presupuesto getPresupuesto() {
        return presupuesto;
    }

    public void setPresupuesto(Presupuesto presupuesto) {
        this.presupuesto = presupuesto;
    }

    public Remito getRemito() {
        return remito;
    }

    public void setRemito(Remito remito) {
        this.remito = remito;
    }

    public Integer getPuntoVenta() {
        return puntoVenta;
    }

    public void setPuntoVenta(Integer puntoVenta) {
        this.puntoVenta = puntoVenta;
    }

    public String getCondicionIvaCliente() {
        return condicionIvaCliente;
    }

    public void setCondicionIvaCliente(String condicionIvaCliente) {
        this.condicionIvaCliente = condicionIvaCliente;
    }

    public String getCae() {
        return cae;
    }

    public void setCae(String cae) {
        this.cae = cae;
    }

    public LocalDate getCaeVencimiento() {
        return caeVencimiento;
    }

    public void setCaeVencimiento(LocalDate caeVencimiento) {
        this.caeVencimiento = caeVencimiento;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public List<DetalleFactura> getLineas() {
        return lineas;
    }

    public void setLineas(List<DetalleFactura> lineas) {
        this.lineas = lineas;
    }
}
