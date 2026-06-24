package com.novatech.store.exception;

// Excepcion para cuando se rompe una REGLA DE NEGOCIO (no un error de formato).
// Por ejemplo: pagar mas que el total del pedido, o un plan de cuotas que no
// corresponde al usuario del pedido. El GlobalExceptionHandler la convierte en 400.
/**
 * Manejo de excepciones `ReglaNegocioException`: respuestas HTTP uniformes ante errores.
 */
public class ReglaNegocioException extends RuntimeException {

    public ReglaNegocioException(String message) {
        super(message);
    }
}
