package com.novatech.store.exception;

// Excepcion para cuando un usuario intenta hacer algo que NO le corresponde por su rol.
// Por ejemplo: un CLIENTE intentando autorizar un pago (solo puede un ADMIN).
// El GlobalExceptionHandler la convierte en 403 (prohibido).
/**
 * Manejo de excepciones `AccesoDenegadoException`: respuestas HTTP uniformes ante errores.
 */
public class AccesoDenegadoException extends RuntimeException {

    public AccesoDenegadoException(String message) {
        super(message);
    }
}
