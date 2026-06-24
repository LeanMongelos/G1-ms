package com.novatech.store.controller;

import com.novatech.store.dto.CrmResumenResponse;
import com.novatech.store.service.CrmService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/crm")
/**
 * Controlador REST `CrmController`: expone endpoints HTTP JSON para Crm. Ruta base en `@RequestMapping` de la clase.
 */
public class CrmController {

    private final CrmService service;

    public CrmController(CrmService service) {
        this.service = service;
    }

    @GetMapping("/resumen")
    public CrmResumenResponse resumen() {
        return service.resumen();
    }
}
