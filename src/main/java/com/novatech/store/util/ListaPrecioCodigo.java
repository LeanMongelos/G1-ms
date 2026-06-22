package com.novatech.store.util;

/** Códigos de listas de precio por canal / segmento comercial. */
public final class ListaPrecioCodigo {

    public static final String MAYORISTA = "MAYORISTA";
    public static final String B2B = "B2B";
    public static final String ECOMMERCE = "ECOMMERCE";
    public static final String LOCAL = "LOCAL";

    private ListaPrecioCodigo() {
    }

    /** Menor = mayor descuento permitido (precio más bajo). */
    public static int jerarquia(String codigo) {
        if (codigo == null) {
            return 99;
        }
        return switch (codigo.trim().toUpperCase()) {
            case MAYORISTA -> 1;
            case B2B -> 2;
            case ECOMMERCE -> 3;
            case LOCAL -> 4;
            default -> 99;
        };
    }

    public static boolean esValido(String codigo) {
        return jerarquia(codigo) < 99;
    }

    public static String etiqueta(String codigo) {
        if (codigo == null) {
            return "";
        }
        return switch (codigo.trim().toUpperCase()) {
            case MAYORISTA -> "Mayorista";
            case B2B -> "B2B / Empresa";
            case ECOMMERCE -> "E-commerce";
            case LOCAL -> "Local / POS";
            default -> codigo;
        };
    }
}
