package com.novatech.store.controller;

import com.novatech.store.dto.ConversacionResumenResponse;
import com.novatech.store.entity.Conversacion;
import com.novatech.store.entity.MensajeConversacion;
import com.novatech.store.service.ConversacionService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/crm/conversaciones")
/**
 * Controlador REST `ConversacionController`: expone endpoints HTTP JSON para Conversacion. Ruta base en `@RequestMapping` de la clase.
 */
public class ConversacionController {

    private final ConversacionService service;

    public ConversacionController(ConversacionService service) {
        this.service = service;
    }

    @GetMapping("/resumen")
    public ConversacionResumenResponse resumen() {
        return service.resumen();
    }

    @GetMapping
    public List<Conversacion> listar(
            @RequestParam(required = false) String canal,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String busqueda) {
        return service.listar(canal, estado, busqueda);
    }

    @GetMapping("/{id}")
    public Conversacion obtener(@PathVariable Integer id) {
        return service.obtener(id);
    }

    @GetMapping("/{id}/mensajes")
    public List<MensajeConversacion> mensajes(@PathVariable Integer id) {
        return service.mensajes(id);
    }

    @PostMapping("/{id}/mensajes")
    public ResponseEntity<MensajeConversacion> enviar(
            @PathVariable Integer id,
            @RequestBody MensajeConversacion mensaje) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.enviarMensaje(id, mensaje));
    }

    @PutMapping("/{id}")
    public Conversacion actualizar(@PathVariable Integer id, @RequestBody Conversacion datos) {
        return service.actualizar(id, datos);
    }
}
