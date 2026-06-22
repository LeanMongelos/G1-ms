package com.novatech.store.controller;

import com.novatech.store.entity.ConfiguracionSistema;
import com.novatech.store.service.ConfiguracionService;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/configuracion")
public class ConfiguracionController {

    private final ConfiguracionService service;

    public ConfiguracionController(ConfiguracionService service) {
        this.service = service;
    }

    @GetMapping("/{grupo}")
    public List<ConfiguracionSistema> listarGrupo(@PathVariable String grupo) {
        return service.listarPorGrupo(grupo);
    }

    @GetMapping("/{grupo}/mapa")
    public Map<String, String> mapaGrupo(@PathVariable String grupo) {
        return service.mapaPorGrupo(grupo);
    }

    @PutMapping("/{grupo}")
    public List<ConfiguracionSistema> guardarGrupo(
            @PathVariable String grupo,
            @RequestBody Map<String, String> valores) {
        return service.guardarGrupo(grupo, valores);
    }
}
