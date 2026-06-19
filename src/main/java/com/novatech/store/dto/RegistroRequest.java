package com.novatech.store.dto;

// Anotaciones de Bean Validation (Jakarta) para validar los datos de entrada.
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Un DTO (Data Transfer Object) es un objeto simple que usamos SOLO para transportar
// datos entre el frontend y el backend. Este representa lo que llega en el cuerpo (body)
// del pedido POST /auth/register cuando alguien se quiere registrar.
//
// Usamos un "record" de Java: es una forma corta de crear una clase que solo guarda datos.
// Java nos genera solo el constructor y los metodos para leer cada campo (nombre(), email(), etc.).
public record RegistroRequest(
        // Nombre y apellido que escribe la persona. No puede ir vacio.
        @NotBlank(message = "El nombre es obligatorio.")
        String nombre,
        // Correo electronico con el que se va a registrar (tiene que ser unico).
        // Tiene que venir y tener formato de email valido.
        @NotBlank(message = "El email es obligatorio.")
        @Email(message = "El email no tiene un formato valido.")
        String email,
        // Contrasena en texto plano (el backend la va a hashear antes de guardarla).
        // Es obligatoria y debe tener al menos 6 caracteres.
        @NotBlank(message = "La contrasena es obligatoria.")
        @Size(min = 6, message = "La contrasena debe tener al menos 6 caracteres.")
        String contrasena) {
}
