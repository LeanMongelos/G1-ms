package com.novatech.store.controller;

import com.novatech.store.dto.ConfirmarOrdenRequest;
import com.novatech.store.dto.ConfirmarOrdenResponse;
import com.novatech.store.security.SecurityUtils;
import com.novatech.store.service.OrdenVentaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ordenes")
public class OrdenVentaController {

    private final OrdenVentaService service;

    public OrdenVentaController(OrdenVentaService service) {
        this.service = service;
    }

    @PostMapping("/confirmar")
    public ResponseEntity<ConfirmarOrdenResponse> confirmar(@Valid @RequestBody ConfirmarOrdenRequest request) {
        var u = SecurityUtils.requerirAutenticado();
        if (!SecurityUtils.esStaff(u.rol())) {
            SecurityUtils.requerirPropioUsuario(request.getIdUsuario());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(service.confirmar(request));
    }
}
