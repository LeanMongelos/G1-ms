package com.novatech.store.exception;

// Esta es una excepcion (un tipo de error) que creamos nosotros.
// La usamos cuando buscamos algo en la base y no existe (por ejemplo un producto con id 999).
// Al extender RuntimeException no estamos obligados a poner try/catch en todos lados.
public class ResourceNotFoundException extends RuntimeException {

    // El constructor recibe el mensaje que explica que no se encontro.
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
