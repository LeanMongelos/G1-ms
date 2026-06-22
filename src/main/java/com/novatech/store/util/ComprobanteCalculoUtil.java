package com.novatech.store.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class ComprobanteCalculoUtil {

    public static final BigDecimal IVA_DEFAULT_RATE = new BigDecimal("0.21");

    private ComprobanteCalculoUtil() {
    }

    public static BigDecimal tasaIvaParaCondicion(String condicionIva) {
        if (condicionIva == null || condicionIva.isBlank()) {
            return IVA_DEFAULT_RATE;
        }
        return switch (condicionIva.toUpperCase()) {
            case "EXENTO", "SUJETO_EXENTO", "IVA_NO_ALCANZADO" -> BigDecimal.ZERO;
            default -> IVA_DEFAULT_RATE;
        };
    }

    public static BigDecimal calcularSubtotalLinea(int cantidad, BigDecimal precioUnitario, BigDecimal descuentoPct) {
        BigDecimal bruto = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
        if (descuentoPct != null && descuentoPct.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal factor = BigDecimal.ONE.subtract(
                    descuentoPct.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP));
            bruto = bruto.multiply(factor);
        }
        return bruto.setScale(2, RoundingMode.HALF_UP);
    }

    public static TotalesComprobante calcularTotales(BigDecimal subtotalNeto, BigDecimal tasaIva) {
        BigDecimal iva = subtotalNeto.multiply(tasaIva).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotalNeto.add(iva).setScale(2, RoundingMode.HALF_UP);
        return new TotalesComprobante(subtotalNeto, iva, total);
    }

    public record TotalesComprobante(BigDecimal subtotal, BigDecimal iva, BigDecimal total) {
    }
}
