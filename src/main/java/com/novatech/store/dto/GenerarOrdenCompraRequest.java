package com.novatech.store.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import java.util.List;

public class GenerarOrdenCompraRequest {

    @NotEmpty(message = "Debe indicar al menos un producto.")
    private List<@Positive(message = "Cada id de producto debe ser positivo.") Integer> productoIds;

    public List<Integer> getProductoIds() {
        return productoIds;
    }

    public void setProductoIds(List<Integer> productoIds) {
        this.productoIds = productoIds;
    }
}
