package com.novatech.store.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO `AdminBuscarResponse`: objeto de transferencia (entrada/salida) sin mapeo directo 1:1 a tabla.
 */
public class AdminBuscarResponse {

    private List<AdminBuscarItemDto> clientes = new ArrayList<>();
    private List<AdminBuscarItemDto> facturas = new ArrayList<>();
    private List<AdminBuscarItemDto> remitos = new ArrayList<>();
    private List<AdminBuscarItemDto> presupuestos = new ArrayList<>();

    public List<AdminBuscarItemDto> getClientes() {
        return clientes;
    }

    public void setClientes(List<AdminBuscarItemDto> clientes) {
        this.clientes = clientes;
    }

    public List<AdminBuscarItemDto> getFacturas() {
        return facturas;
    }

    public void setFacturas(List<AdminBuscarItemDto> facturas) {
        this.facturas = facturas;
    }

    public List<AdminBuscarItemDto> getRemitos() {
        return remitos;
    }

    public void setRemitos(List<AdminBuscarItemDto> remitos) {
        this.remitos = remitos;
    }

    public List<AdminBuscarItemDto> getPresupuestos() {
        return presupuestos;
    }

    public void setPresupuestos(List<AdminBuscarItemDto> presupuestos) {
        this.presupuestos = presupuestos;
    }
}
