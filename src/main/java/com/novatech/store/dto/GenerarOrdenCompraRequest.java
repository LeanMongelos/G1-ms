package com.novatech.store.dto;

import java.util.List;

public class GenerarOrdenCompraRequest {

    private List<Integer> productoIds;

    public List<Integer> getProductoIds() {
        return productoIds;
    }

    public void setProductoIds(List<Integer> productoIds) {
        this.productoIds = productoIds;
    }
}
