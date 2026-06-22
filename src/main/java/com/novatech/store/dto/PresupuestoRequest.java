package com.novatech.store.dto;

import com.novatech.store.entity.Presupuesto;
import java.time.LocalDate;
import java.util.List;

public class PresupuestoRequest {

    private Integer idCliente;
    private LocalDate validezHasta;
    private String notas;
    private List<LineaComprobanteDto> lineas;

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public LocalDate getValidezHasta() {
        return validezHasta;
    }

    public void setValidezHasta(LocalDate validezHasta) {
        this.validezHasta = validezHasta;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public List<LineaComprobanteDto> getLineas() {
        return lineas;
    }

    public void setLineas(List<LineaComprobanteDto> lineas) {
        this.lineas = lineas;
    }

    public static PresupuestoRequest fromEntity(Presupuesto p) {
        PresupuestoRequest req = new PresupuestoRequest();
        if (p.getCliente() != null) {
            req.setIdCliente(p.getCliente().getIdCliente());
        }
        req.setValidezHasta(p.getValidezHasta());
        req.setNotas(p.getNotas());
        return req;
    }
}
