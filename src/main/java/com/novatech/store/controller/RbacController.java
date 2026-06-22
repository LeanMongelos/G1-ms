package com.novatech.store.controller;

import com.novatech.store.entity.RolRbac;
import com.novatech.store.service.RbacService;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/configuracion/rbac")
public class RbacController {

    private final RbacService service;

    public RbacController(RbacService service) {
        this.service = service;
    }

    @GetMapping("/matriz")
    public Map<String, Object> matriz() {
        return service.matriz();
    }

    @GetMapping("/roles")
    public List<RolRbac> listarRoles() {
        return service.listarRoles();
    }

    @PostMapping("/roles")
    public ResponseEntity<RolRbac> crearRol(@RequestBody Map<String, String> body) {
        RolRbac creado = service.crearRol(
                body.getOrDefault("clave", ""),
                body.get("nombre"),
                body.get("descripcion"));
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/roles/{clave}")
    public RolRbac actualizarMeta(@PathVariable String clave, @RequestBody Map<String, String> body) {
        return service.actualizarRolMeta(clave, body.get("nombre"), body.get("descripcion"));
    }

    @PatchMapping("/roles/{clave}")
    public void actualizarPermisos(@PathVariable String clave, @RequestBody Map<String, List<String>> body) {
        service.actualizarPermisosRol(clave.toUpperCase(), body.getOrDefault("permisos", List.of()));
    }

    @DeleteMapping("/roles/{clave}")
    public ResponseEntity<Void> eliminarRol(@PathVariable String clave) {
        service.eliminarRol(clave.toUpperCase());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/permisos-usuario/{rol}")
    public List<String> permisosUsuario(@PathVariable String rol) {
        return service.permisosDeRol(rol.toUpperCase());
    }
}
