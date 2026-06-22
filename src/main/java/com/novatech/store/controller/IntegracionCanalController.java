package com.novatech.store.controller;

import com.novatech.store.dto.IntegracionCanalResponse;
import com.novatech.store.dto.UsuarioResponse;
import com.novatech.store.entity.IntegracionCanal;
import com.novatech.store.security.SecurityUtils;
import com.novatech.store.service.IntegracionCanalService;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/crm/integraciones")
public class IntegracionCanalController {

    private final IntegracionCanalService service;

    public IntegracionCanalController(IntegracionCanalService service) {
        this.service = service;
    }

    @GetMapping
    public List<IntegracionCanalResponse> listar() {
        SecurityUtils.requerirStaff();
        return service.listarEnmascaradas();
    }

    @PutMapping("/{id}")
    public IntegracionCanalResponse actualizar(@PathVariable Integer id, @RequestBody IntegracionCanal datos) {
        UsuarioResponse u = SecurityUtils.requerirAutenticado();
        if (!SecurityUtils.esStaff(u.rol())) {
            SecurityUtils.requerirStaff();
        }
        return service.actualizarEnmascarada(id, datos);
    }
}
