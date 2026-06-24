package com.novatech.store.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Remito")
/**
 * Entidad JPA `Remito`: tabla y relaciones ORM; se serializa a JSON en respuestas API.
 */
public class Remito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_remito")
    private Integer idRemito;

    @Column(name = "numero_remito", unique = true)
    private String numeroRemito;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_pedido")
    @JsonIgnoreProperties({"usuario", "notas"})
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_presupuesto")
    @JsonIgnoreProperties({"lineas", "cliente", "notas"})
    private Presupuesto presupuesto;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_cliente")
    @JsonIgnoreProperties({"notas", "sitioWeb", "lat", "lng", "historialCrediticio"})
    private PerfilCliente cliente;

    @Column(name = "fecha")
    private LocalDateTime fecha;

    /** PREPARADO, DESPACHADO, ENTREGADO */
    @Column(name = "estado")
    private String estado;

    @Column(name = "direccion_entrega")
    private String direccionEntrega;

    @Column(name = "notas", columnDefinition = "TEXT")
    private String notas;

    @OneToMany(mappedBy = "remito", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<DetalleRemito> lineas = new ArrayList<>();

    public Remito() {
    }

    public Integer getIdRemito() {
        return idRemito;
    }

    public void setIdRemito(Integer idRemito) {
        this.idRemito = idRemito;
    }

    public String getNumeroRemito() {
        return numeroRemito;
    }

    public void setNumeroRemito(String numeroRemito) {
        this.numeroRemito = numeroRemito;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public Presupuesto getPresupuesto() {
        return presupuesto;
    }

    public void setPresupuesto(Presupuesto presupuesto) {
        this.presupuesto = presupuesto;
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getDireccionEntrega() {
        return direccionEntrega;
    }

    public void setDireccionEntrega(String direccionEntrega) {
        this.direccionEntrega = direccionEntrega;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public List<DetalleRemito> getLineas() {
        return lineas;
    }

    public void setLineas(List<DetalleRemito> lineas) {
        this.lineas = lineas;
    }
}
