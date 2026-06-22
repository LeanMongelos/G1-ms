package com.novatech.store.controller;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Health check compatible con monitoreo y load balancers.
 * Expone {@code GET /actuator/health} sin depender del módulo Actuator completo.
 */
@RestController
public class HealthController {

    private final DataSource dataSource;

    public HealthController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/actuator/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", "UP");
        body.put("service", "novatech-backend");

        Map<String, String> components = new LinkedHashMap<>();
        try (var conn = dataSource.getConnection()) {
            if (conn.isValid(2)) {
                components.put("db", "UP");
            } else {
                components.put("db", "DOWN");
                body.put("status", "DOWN");
            }
        } catch (Exception ex) {
            components.put("db", "DOWN");
            body.put("status", "DOWN");
            body.put("error", ex.getMessage());
        }
        body.put("components", components);

        HttpStatus code = "UP".equals(body.get("status")) ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE;
        return ResponseEntity.status(code).body(body);
    }
}
