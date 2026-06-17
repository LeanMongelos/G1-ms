package com.novatech.store.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// @RestControllerAdvice atrapa los errores de TODOS los controllers en un solo lugar.
// Asi, cuando algo falla, el frontend siempre recibe un JSON ordenado en vez de un error feo.
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Cuando alguien busca algo que no existe, respondemos con codigo 404 (no encontrado).
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex) {
        // Armamos un objeto (un mapa) con la informacion del error.
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString()); // cuando paso
        body.put("status", HttpStatus.NOT_FOUND.value());       // el numero 404
        body.put("error", "Not Found");                          // texto del error
        body.put("message", ex.getMessage());                    // el detalle que escribimos nosotros
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    // Cuando los datos que mandan no cumplen las validaciones, respondemos con 400 (pedido mal hecho).
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        // Recorremos todos los campos que estan mal y guardamos el motivo de cada uno.
        Map<String, Object> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(e -> errors.put(e.getField(), e.getDefaultMessage()));

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", HttpStatus.BAD_REQUEST.value()); // el numero 400
        body.put("error", "Validation Failed");
        body.put("fields", errors); // que campos fallaron y por que
        return ResponseEntity.badRequest().body(body);
    }
}
