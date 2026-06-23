package com.novatech.store.util;

import com.novatech.store.entity.Producto;

public final class StockInventarioUtil {

    public static final int STOCK_MINIMO_DEFAULT = 5;

    private StockInventarioUtil() {
    }

    public static int stockMinimoEfectivo(Producto p) {
        return p.getStockMinimo() != null && p.getStockMinimo() > 0
                ? p.getStockMinimo() : STOCK_MINIMO_DEFAULT;
    }

    public static int stockActual(Producto p) {
        return p.getStock() != null ? p.getStock() : 0;
    }

    public static boolean esStockBajo(Producto p) {
        return stockActual(p) <= stockMinimoEfectivo(p);
    }
}
