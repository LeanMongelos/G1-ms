package com.novatech.store.controller;

import com.novatech.store.dto.DashboardKpiResponse;
import com.novatech.store.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
/**
 * Controlador REST `DashboardController`: expone endpoints HTTP JSON para Dashboard. Ruta base en `@RequestMapping` de la clase.
 */
public class DashboardController {

    private final DashboardService service;

    public DashboardController(DashboardService service) {
        this.service = service;
    }

    @GetMapping("/kpis")
    public DashboardKpiResponse kpis() {
        return service.kpis();
    }
}
