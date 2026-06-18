package com.novatech.store.dto;

// Un DTO (Data Transfer Object) es un objeto simple que usamos SOLO para transportar
// datos entre el frontend y el backend. Este representa lo que llega en el cuerpo (body)
// del pedido POST /auth/register cuando alguien se quiere registrar.
//
// Usamos un "record" de Java: es una forma corta de crear una clase que solo guarda datos.
// Java nos genera solo el constructor y los metodos para leer cada campo (nombre(), email(), etc.).
public record RegistroRequest(
        // Nombre y apellido que escribe la persona.
        String nombre,
        // Correo electronico con el que se va a registrar (tiene que ser unico).
        String email,
        // Contrasena en texto plano (el backend la va a hashear antes de guardarla).
        String contrasena) {
}
