package com.novatech.store.controller;

import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Este controller es solo para probar que el backend esta vivo y respondiendo.
// El frontend puede llamarlo para verificar que se puede conectar.
@RestController
@RequestMapping("/ping")
/**
 * Controlador REST `PingController`: expone endpoints HTTP JSON para Ping. Ruta base en `@RequestMapping` de la clase.
 */
public class PingController {

    // GET /ping -> devuelve un JSON simple con el estado y la hora actual.
    @GetMapping
    public Map<String, Object> ping() {
        // Map.of arma un objeto JSON con pares clave-valor.
        return Map.of(
                "status", "ok",
                "service", "novatech-backend",
                "timestamp", LocalDateTime.now().toString());
    }
}
