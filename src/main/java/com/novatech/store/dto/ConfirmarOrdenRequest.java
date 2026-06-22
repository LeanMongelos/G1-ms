package com.novatech.store.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public class ConfirmarOrdenRequest {

    @NotNull(message = "El id del usuario es obligatorio.")
    private Integer idUsuario;

    @NotEmpty(message = "El pedido debe tener al menos una linea.")
    @Valid
    private List<ConfirmarOrdenLineaRequest> lineas;

    @NotBlank(message = "El metodo de pago es obligatorio.")
    private String metodoPago;

    /** ENVIO o RETIRO_TIENDA. Por defecto ENVIO. */
    private String tipoEntrega;

    /** WEB, ADMIN, WHATSAPP, EMAIL, INSTAGRAM, FACEBOOK, POS. Por defecto WEB. */
    private String canalOrigen;

    /** MAYORISTA, B2B, ECOMMERCE, LOCAL — opcional; si no se envía se infiere por canal/tipo cliente. */
    private String codigoListaPrecio;

    private String direccionEnvio;
    private String empresaLogistica;
    private String notas;
    private String proveedorBilletera;
    private String referencia;

    /** Solo para PRESTAMO_CASA */
    private Integer cantidadCuotas;
    private BigDecimal interes;

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public List<ConfirmarOrdenLineaRequest> getLineas() {
        return lineas;
    }

    public void setLineas(List<ConfirmarOrdenLineaRequest> lineas) {
        this.lineas = lineas;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getTipoEntrega() {
        return tipoEntrega;
    }

    public void setTipoEntrega(String tipoEntrega) {
        this.tipoEntrega = tipoEntrega;
    }

    public String getCanalOrigen() {
        return canalOrigen;
    }

    public void setCanalOrigen(String canalOrigen) {
        this.canalOrigen = canalOrigen;
    }

    public String getCodigoListaPrecio() {
        return codigoListaPrecio;
    }

    public void setCodigoListaPrecio(String codigoListaPrecio) {
        this.codigoListaPrecio = codigoListaPrecio;
    }

    public String getDireccionEnvio() {
        return direccionEnvio;
    }

    public void setDireccionEnvio(String direccionEnvio) {
        this.direccionEnvio = direccionEnvio;
    }

    public String getEmpresaLogistica() {
        return empresaLogistica;
    }

    public void setEmpresaLogistica(String empresaLogistica) {
        this.empresaLogistica = empresaLogistica;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
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
}
