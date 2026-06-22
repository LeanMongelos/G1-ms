package com.novatech.store.util;

import com.novatech.store.entity.Producto;
import java.math.BigDecimal;
import java.math.RoundingMode;

public final class PrecioListaUtil {

    private PrecioListaUtil() {
    }

    public static BigDecimal precioBase(Producto producto) {
        if (producto.getPrecioLista() != null && producto.getPrecioLista().compareTo(BigDecimal.ZERO) > 0) {
            return producto.getPrecioLista();
        }
        return producto.getPrecio() != null ? producto.getPrecio() : BigDecimal.ZERO;
    }

    public static BigDecimal normalizarDescuento(BigDecimal descuento) {
        if (descuento == null) {
            return BigDecimal.ZERO;
        }
        if (descuento.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        }
        if (descuento.compareTo(new BigDecimal("100")) > 0) {
            return new BigDecimal("100");
        }
        return descuento.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Precio efectivo: precio fijo unitario, o base menos descuento unitario, o base menos descuento global.
     */
    public static BigDecimal calcularPrecioEfectivo(
            Producto producto,
            BigDecimal descuentoGlobal,
            BigDecimal descuentoUnitario,
            BigDecimal precioFijo) {

        if (precioFijo != null && precioFijo.compareTo(BigDecimal.ZERO) >= 0) {
            return precioFijo.setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal base = precioBase(producto);
        BigDecimal desc = descuentoUnitario != null ? descuentoUnitario : descuentoGlobal;
        desc = normalizarDescuento(desc);

        BigDecimal factor = BigDecimal.ONE.subtract(
                desc.divide(new BigDecimal("100"), 6, RoundingMode.HALF_UP));
        return base.multiply(factor).setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal descuentoEfectivoPorcentaje(Producto producto, BigDecimal precioEfectivo) {
        BigDecimal base = precioBase(producto);
        if (base.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal ratio = precioEfectivo.divide(base, 6, RoundingMode.HALF_UP);
        BigDecimal desc = BigDecimal.ONE.subtract(ratio).multiply(new BigDecimal("100"));
        return desc.max(BigDecimal.ZERO).min(new BigDecimal("100")).setScale(2, RoundingMode.HALF_UP);
    }
}
