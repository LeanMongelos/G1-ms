package com.novatech.store.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.math.BigDecimal;

// Esta clase representa la tabla "PlanCuotas".
// Guarda el plan de pago en cuotas de un pedido para un cliente.
@Entity
@Table(name = "PlanCuotas")
/**
 * Entidad JPA `PlanCuotas`: tabla y relaciones ORM; se serializa a JSON en respuestas API.
 */
public class PlanCuotas {

    // Identificador unico del plan de cuotas.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_plan")
    private Integer idPlan;

    // A que cliente pertenece el plan de cuotas.
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_cliente")
    @JsonIgnoreProperties({"notas", "sitioWeb", "lat", "lng", "historialCrediticio", "usuario"})
    private PerfilCliente cliente;

    // A que pedido corresponde el plan de cuotas.
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_pedido")
    @JsonIgnore
    private Pedido pedido;

    // En cuantas cuotas se va a pagar.
    @Column(name = "cantidad_cuotas")
    private Integer cantidadCuotas;

    // Porcentaje de interes que se aplica.
    @Column(name = "interes", precision = 6, scale = 2)
    private BigDecimal interes;

    // Estado del plan, por ejemplo "ACTIVO", "FINALIZADO", "CANCELADO".
    @Column(name = "estado")
    private String estado;

    // Constructor vacio para JPA.
    public PlanCuotas() {
    }

    // Getters y setters.

    public Integer getIdPlan() {
        return idPlan;
    }

    public void setIdPlan(Integer idPlan) {
        this.idPlan = idPlan;
    }

    public PerfilCliente getCliente() {
        return cliente;
    }

    public void setCliente(PerfilCliente cliente) {
        this.cliente = cliente;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public Integer getCantidadCuotas() {
        return cantidadCuotas;
    }

    public void setCantidadCuotas(Integer cantidadCuotas) {
        this.cantidadCuotas = cantidadCuotas;
    }

    public BigDecimal getInteres() {
        return interes;
    }

    public void setInteres(BigDecimal interes) {
        this.interes = interes;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
