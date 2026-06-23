package com.novatech.store.controller;

import com.novatech.store.dto.ActualizarPerfilClienteRequest;
import com.novatech.store.dto.CrearDevolucionRequest;
import com.novatech.store.dto.CrearTicketClienteRequest;
import com.novatech.store.dto.EnviarMensajeTicketRequest;
import com.novatech.store.dto.PedidoDetalleResponse;
import com.novatech.store.entity.Conversacion;
import com.novatech.store.entity.Factura;
import com.novatech.store.entity.MensajeConversacion;
import com.novatech.store.entity.Pedido;
import com.novatech.store.entity.PerfilCliente;
import com.novatech.store.entity.SolicitudDevolucion;
import com.novatech.store.service.ClientePortalService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cliente")
public class ClientePortalController {

    private final ClientePortalService service;

    public ClientePortalController(ClientePortalService service) {
        this.service = service;
    }

    @GetMapping("/pedidos")
    public List<Pedido> listarPedidos() {
        return service.listarPedidos();
    }

    @GetMapping("/pedidos/{id}")
    public PedidoDetalleResponse obtenerPedido(@PathVariable Integer id) {
        return service.obtenerPedido(id);
    }

    @GetMapping("/facturas")
    public List<Factura> listarFacturas() {
        return service.listarFacturas();
    }

    @GetMapping("/facturas/{id}")
    public Factura obtenerFactura(@PathVariable Integer id) {
        return service.obtenerFactura(id);
    }

    @GetMapping("/perfil")
    public PerfilCliente obtenerPerfil() {
        return service.obtenerPerfil();
    }

    @PutMapping("/perfil")
    public PerfilCliente actualizarPerfil(@Valid @RequestBody ActualizarPerfilClienteRequest datos) {
        return service.actualizarPerfil(datos);
    }

    @GetMapping("/tickets")
    public List<Conversacion> listarTickets() {
        return service.listarTickets();
    }

    @PostMapping("/tickets")
    public ResponseEntity<Conversacion> crearTicket(@Valid @RequestBody CrearTicketClienteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crearTicket(request));
    }

    @GetMapping("/tickets/{id}/mensajes")
    public List<MensajeConversacion> listarMensajesTicket(@PathVariable Integer id) {
        return service.listarMensajesTicket(id);
    }

    @PostMapping("/tickets/{id}/mensajes")
    public ResponseEntity<MensajeConversacion> responderTicket(
            @PathVariable Integer id,
            @Valid @RequestBody EnviarMensajeTicketRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.responderTicket(id, request));
    }

    @GetMapping("/devoluciones")
    public List<SolicitudDevolucion> listarDevoluciones() {
        return service.listarDevoluciones();
    }

    @PostMapping("/devoluciones")
    public ResponseEntity<SolicitudDevolucion> crearDevolucion(
            @Valid @RequestBody CrearDevolucionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crearDevolucion(request));
    }

    @GetMapping("/devoluciones/{id}")
    public SolicitudDevolucion obtenerDevolucion(@PathVariable Integer id) {
        return service.obtenerDevolucion(id);
    }
}
