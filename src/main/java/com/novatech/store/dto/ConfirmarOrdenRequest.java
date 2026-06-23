package com.novatech.store.dto;

import com.novatech.store.validation.ValidationPatterns;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

public class ConfirmarOrdenRequest {

    @NotNull(message = "El id del usuario es obligatorio.")
    private Integer idUsuario;

    @NotEmpty(message = "El pedido debe tener al menos una linea.")
    @Valid
    private List<ConfirmarOrdenLineaRequest> lineas;

    @NotBlank(message = "El metodo de pago es obligatorio.")
    @Pattern(regexp = ValidationPatterns.METODO_PAGO, message = "Metodo de pago invalido.")
    private String metodoPago;

    @Pattern(regexp = ValidationPatterns.TIPO_ENTREGA, message = "Tipo de entrega invalido.")
    private String tipoEntrega;

    @Pattern(regexp = ValidationPatterns.CANAL_ORIGEN, message = "Canal de origen invalido.")
    private String canalOrigen;

    @Size(max = 50, message = "El codigo de lista no puede superar 50 caracteres.")
    private String codigoListaPrecio;

    @Size(min = 10, max = 300, message = "La direccion debe tener entre 10 y 300 caracteres.")
    private String direccionEnvio;

    @Size(max = 100, message = "La empresa logistica no puede superar 100 caracteres.")
    private String empresaLogistica;

    @Size(max = 500, message = "Las notas no pueden superar 500 caracteres.")
    private String notas;

    @Size(max = 100, message = "El proveedor de billetera no puede superar 100 caracteres.")
    private String proveedorBilletera;

    @Size(max = 100, message = "La referencia no puede superar 100 caracteres.")
    private String referencia;

    @Min(value = 1, message = "Las cuotas deben ser al menos 1.")
    @Max(value = 24, message = "Las cuotas no pueden superar 24.")
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
