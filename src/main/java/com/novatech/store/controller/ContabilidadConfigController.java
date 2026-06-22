package com.novatech.store.controller;

import com.novatech.store.service.ContabilidadConfigService;
import java.util.Map;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/config/contabilidad")
public class ContabilidadConfigController {

    private final ContabilidadConfigService service;

    public ContabilidadConfigController(ContabilidadConfigService service) {
        this.service = service;
    }

    @GetMapping("/resumen")
    public Map<String, Object> resumen() {
        return service.resumen();
    }

    @PostMapping("/resumen")
    public Map<String, Object> restaurar() {
        return service.ensureContabilidadArgentina();
    }
}
