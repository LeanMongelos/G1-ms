package com.novatech.store.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Convierte montos ARS a texto (es-AR simplificado). */
public final class NumeroALetrasUtil {

    private static final String[] UNIDADES = {
            "", "uno", "dos", "tres", "cuatro", "cinco", "seis", "siete", "ocho", "nueve",
            "diez", "once", "doce", "trece", "catorce", "quince", "dieciséis", "diecisiete",
            "dieciocho", "diecinueve"
    };
    private static final String[] DECENAS = {
            "", "", "veinte", "treinta", "cuarenta", "cincuenta", "sesenta", "setenta", "ochenta", "noventa"
    };
    private static final String[] CENTENAS = {
            "", "ciento", "doscientos", "trescientos", "cuatrocientos", "quinientos",
            "seiscientos", "setecientos", "ochocientos", "novecientos"
    };

    private NumeroALetrasUtil() {
    }

    public static String pesos(BigDecimal monto) {
        if (monto == null) {
            return "cero pesos";
        }
        BigDecimal redondeado = monto.setScale(2, RoundingMode.HALF_UP);
        long enteros = redondeado.longValue();
        int centavos = redondeado.remainder(BigDecimal.ONE).movePointRight(2).intValue();
        String texto = convertirEntero(enteros);
        if (centavos > 0) {
            return texto + " pesos con " + convertirEntero(centavos) + " centavos";
        }
        return texto + " pesos";
    }

    private static String convertirEntero(long n) {
        if (n == 0) {
            return "cero";
        }
        if (n == 100) {
            return "cien";
        }
        if (n < 20) {
            return UNIDADES[(int) n];
        }
        if (n < 100) {
            int d = (int) (n / 10);
            int u = (int) (n % 10);
            if (n == 20) {
                return "veinte";
            }
            if (n < 30) {
                return "veinti" + UNIDADES[u];
            }
            return DECENAS[d] + (u > 0 ? " y " + UNIDADES[u] : "");
        }
        if (n < 1000) {
            int c = (int) (n / 100);
            int resto = (int) (n % 100);
            return CENTENAS[c] + (resto > 0 ? " " + convertirEntero(resto) : "");
        }
        if (n < 1_000_000) {
            long miles = n / 1000;
            long resto = n % 1000;
            String pref = miles == 1 ? "mil" : convertirEntero(miles) + " mil";
            return pref + (resto > 0 ? " " + convertirEntero(resto) : "");
        }
        long millones = n / 1_000_000;
        long resto = n % 1_000_000;
        String pref = millones == 1 ? "un millón" : convertirEntero(millones) + " millones";
        return pref + (resto > 0 ? " " + convertirEntero(resto) : "");
    }
}
