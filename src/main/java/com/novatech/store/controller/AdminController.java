package com.novatech.store.controller;

import com.novatech.store.dto.AdminBuscarResponse;
import com.novatech.store.dto.AdminNotificacionDto;
import com.novatech.store.service.AdminService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
/**
 * Controlador REST `AdminController`: expone endpoints HTTP JSON para Admin. Ruta base en `@RequestMapping` de la clase.
 */
public class AdminController {

    private final AdminService service;

    public AdminController(AdminService service) {
        this.service = service;
    }

    @GetMapping("/buscar")
    public AdminBuscarResponse buscar(@RequestParam(name = "q", required = false) String q) {
        return service.buscar(q);
    }

    @GetMapping("/notificaciones")
    public List<AdminNotificacionDto> notificaciones() {
        return service.notificaciones();
    }
}
