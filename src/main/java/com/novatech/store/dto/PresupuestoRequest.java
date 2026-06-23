package com.novatech.store.dto;

import com.novatech.store.entity.Presupuesto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

public class PresupuestoRequest {

    @NotNull(message = "El id del cliente es obligatorio.")
    @Positive(message = "El id del cliente debe ser positivo.")
    private Integer idCliente;

    private LocalDate validezHasta;

    @Size(max = 500, message = "Las notas no pueden superar 500 caracteres.")
    private String notas;

    @NotEmpty(message = "El presupuesto debe tener al menos una linea.")
    @Valid
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
