package com.novatech.store.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DashboardKpiResponse {

    // --- Ventas ---
    private BigDecimal ventasTotales = BigDecimal.ZERO;
    private BigDecimal ventasMes = BigDecimal.ZERO;
    private BigDecimal ventasHoy = BigDecimal.ZERO;
    private long pedidosTotal;
    private long pedidosMes;
    private long pedidosHoy;
    private long pedidosPendientes;
    private long pedidosPagados;
    private BigDecimal ticketPromedio = BigDecimal.ZERO;

    // --- Facturación ---
    private BigDecimal facturadoTotal = BigDecimal.ZERO;
    private BigDecimal facturadoMes = BigDecimal.ZERO;
    private BigDecimal ivaMes = BigDecimal.ZERO;
    private long facturasEmitidas;
    private long facturasAnuladas;

    // --- Cobranza ---
    private BigDecimal cobradoTotal = BigDecimal.ZERO;
    private BigDecimal cobradoMes = BigDecimal.ZERO;
    private long pagosRegistrados;
    private BigDecimal carteraPendiente = BigDecimal.ZERO;

    // --- Catálogo ---
    private long productosTotal;
    private long productosBajoStock;

    // --- CRM / crédito ---
    private long clientesActivos;
    private long cuotasVencidas;
    private long cuotasPorVencer;
    private long promocionesActivas;
    private long campanasPendientes;
    private long pagosPendientesAprobar;
    private long crmPendientes;

    private List<EstadoCantidadDto> pedidosPorEstado = new ArrayList<>();
    private List<PedidoRecienteDto> ultimosPedidos = new ArrayList<>();
    private List<FacturaRecienteDto> ultimasFacturas = new ArrayList<>();

    public BigDecimal getVentasTotales() {
        return ventasTotales;
    }

    public void setVentasTotales(BigDecimal ventasTotales) {
        this.ventasTotales = ventasTotales;
    }

    public BigDecimal getVentasMes() {
        return ventasMes;
    }

    public void setVentasMes(BigDecimal ventasMes) {
        this.ventasMes = ventasMes;
    }

    public BigDecimal getVentasHoy() {
        return ventasHoy;
    }

    public void setVentasHoy(BigDecimal ventasHoy) {
        this.ventasHoy = ventasHoy;
    }

    public long getPedidosTotal() {
        return pedidosTotal;
    }

    public void setPedidosTotal(long pedidosTotal) {
        this.pedidosTotal = pedidosTotal;
    }

    public long getPedidosMes() {
        return pedidosMes;
    }

    public void setPedidosMes(long pedidosMes) {
        this.pedidosMes = pedidosMes;
    }

    public long getPedidosHoy() {
        return pedidosHoy;
    }

    public void setPedidosHoy(long pedidosHoy) {
        this.pedidosHoy = pedidosHoy;
    }

    public long getPedidosPendientes() {
        return pedidosPendientes;
    }

    public void setPedidosPendientes(long pedidosPendientes) {
        this.pedidosPendientes = pedidosPendientes;
    }

    public long getPedidosPagados() {
        return pedidosPagados;
    }

    public void setPedidosPagados(long pedidosPagados) {
        this.pedidosPagados = pedidosPagados;
    }

    public BigDecimal getTicketPromedio() {
        return ticketPromedio;
    }

    public void setTicketPromedio(BigDecimal ticketPromedio) {
        this.ticketPromedio = ticketPromedio;
    }

    public BigDecimal getFacturadoTotal() {
        return facturadoTotal;
    }

    public void setFacturadoTotal(BigDecimal facturadoTotal) {
        this.facturadoTotal = facturadoTotal;
    }

    public BigDecimal getFacturadoMes() {
        return facturadoMes;
    }

    public void setFacturadoMes(BigDecimal facturadoMes) {
        this.facturadoMes = facturadoMes;
    }

    public BigDecimal getIvaMes() {
        return ivaMes;
    }

    public void setIvaMes(BigDecimal ivaMes) {
        this.ivaMes = ivaMes;
    }

    public long getFacturasEmitidas() {
        return facturasEmitidas;
    }

    public void setFacturasEmitidas(long facturasEmitidas) {
        this.facturasEmitidas = facturasEmitidas;
    }

    public long getFacturasAnuladas() {
        return facturasAnuladas;
    }

    public void setFacturasAnuladas(long facturasAnuladas) {
        this.facturasAnuladas = facturasAnuladas;
    }

    public BigDecimal getCobradoTotal() {
        return cobradoTotal;
    }

    public void setCobradoTotal(BigDecimal cobradoTotal) {
        this.cobradoTotal = cobradoTotal;
    }

    public BigDecimal getCobradoMes() {
        return cobradoMes;
    }

    public void setCobradoMes(BigDecimal cobradoMes) {
        this.cobradoMes = cobradoMes;
    }

    public long getPagosRegistrados() {
        return pagosRegistrados;
    }

    public void setPagosRegistrados(long pagosRegistrados) {
        this.pagosRegistrados = pagosRegistrados;
    }

    public BigDecimal getCarteraPendiente() {
        return carteraPendiente;
    }

    public void setCarteraPendiente(BigDecimal carteraPendiente) {
        this.carteraPendiente = carteraPendiente;
    }

    public long getProductosTotal() {
        return productosTotal;
    }

    public void setProductosTotal(long productosTotal) {
        this.productosTotal = productosTotal;
    }

    public long getProductosBajoStock() {
        return productosBajoStock;
    }

    public void setProductosBajoStock(long productosBajoStock) {
        this.productosBajoStock = productosBajoStock;
    }

    public long getClientesActivos() {
        return clientesActivos;
    }

    public void setClientesActivos(long clientesActivos) {
        this.clientesActivos = clientesActivos;
    }

    public long getCuotasVencidas() {
        return cuotasVencidas;
    }

    public void setCuotasVencidas(long cuotasVencidas) {
        this.cuotasVencidas = cuotasVencidas;
    }

    public long getCuotasPorVencer() {
        return cuotasPorVencer;
    }

    public void setCuotasPorVencer(long cuotasPorVencer) {
        this.cuotasPorVencer = cuotasPorVencer;
    }

    public long getPromocionesActivas() {
        return promocionesActivas;
    }

    public void setPromocionesActivas(long promocionesActivas) {
        this.promocionesActivas = promocionesActivas;
    }

    public long getCampanasPendientes() {
        return campanasPendientes;
    }

    public void setCampanasPendientes(long campanasPendientes) {
        this.campanasPendientes = campanasPendientes;
    }

    public long getPagosPendientesAprobar() {
        return pagosPendientesAprobar;
    }

    public void setPagosPendientesAprobar(long pagosPendientesAprobar) {
        this.pagosPendientesAprobar = pagosPendientesAprobar;
    }

    public long getCrmPendientes() {
        return crmPendientes;
    }

    public void setCrmPendientes(long crmPendientes) {
        this.crmPendientes = crmPendientes;
    }

    public List<EstadoCantidadDto> getPedidosPorEstado() {
        return pedidosPorEstado;
    }

    public void setPedidosPorEstado(List<EstadoCantidadDto> pedidosPorEstado) {
        this.pedidosPorEstado = pedidosPorEstado;
    }

    public List<PedidoRecienteDto> getUltimosPedidos() {
        return ultimosPedidos;
    }

    public void setUltimosPedidos(List<PedidoRecienteDto> ultimosPedidos) {
        this.ultimosPedidos = ultimosPedidos;
    }

    public List<FacturaRecienteDto> getUltimasFacturas() {
        return ultimasFacturas;
    }

    public void setUltimasFacturas(List<FacturaRecienteDto> ultimasFacturas) {
        this.ultimasFacturas = ultimasFacturas;
    }
}
