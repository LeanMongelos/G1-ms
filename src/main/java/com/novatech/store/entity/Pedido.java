package com.novatech.store.entity;

import jakarta.persistence.*;
// Anotaciones de Bean Validation (Jakarta) para validar los datos de entrada.
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;

// Esta clase representa la tabla "Pedido".
// Un pedido es una compra que hace un usuario.
@Entity
@Table(name = "Pedido")
public class Pedido {

    // Identificador unico del pedido.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedido")
    private Integer idPedido;

    // El usuario que hizo el pedido.
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    // Fecha y hora del pedido.
    @Column(name = "fecha")
    private LocalDateTime fecha;

    // Estado del pedido, por ejemplo "PENDIENTE", "PAGADO", "ENVIADO".
    @Column(name = "estado")
    private String estado;

    // Monto total a pagar por el pedido. Es obligatorio y debe ser mayor a 0.
    @NotNull(message = "El total es obligatorio.")
    @Positive(message = "El total debe ser mayor a 0.")
    @Column(name = "total", precision = 12, scale = 2)
    private BigDecimal total;

    /** WEB, ADMIN, WHATSAPP, EMAIL, INSTAGRAM, FACEBOOK, POS */
    @Column(name = "canal_origen")
    private String canalOrigen;

    /** ENVIO o RETIRO_TIENDA */
    @Column(name = "tipo_entrega")
    private String tipoEntrega;

    @Column(name = "notas", columnDefinition = "TEXT")
    private String notas;

    // Constructor vacio para JPA.
    public Pedido() {
    }

    // Getters y setters.

    public Integer getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(Integer idPedido) {
        this.idPedido = idPedido;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
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

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getCanalOrigen() {
        return canalOrigen;
    }

    public void setCanalOrigen(String canalOrigen) {
        this.canalOrigen = canalOrigen;
    }

    public String getTipoEntrega() {
        return tipoEntrega;
    }

    public void setTipoEntrega(String tipoEntrega) {
        this.tipoEntrega = tipoEntrega;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }
}
