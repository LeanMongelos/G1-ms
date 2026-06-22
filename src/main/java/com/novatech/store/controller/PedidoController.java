package com.novatech.store.controller;

import com.novatech.store.dto.PedidoDetalleResponse;
import com.novatech.store.dto.UsuarioResponse;
import com.novatech.store.entity.Pedido;
import com.novatech.store.security.SecurityUtils;
import com.novatech.store.service.PedidoService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService service;

    public PedidoController(PedidoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Pedido> listar(
            @RequestParam(required = false) String canalOrigen,
            @RequestParam(required = false) String estado) {
        UsuarioResponse u = SecurityUtils.requerirAutenticado();
        if (!SecurityUtils.esStaff(u.rol())) {
            return service.listarPorUsuario(u.idUsuario());
        }
        return service.listar(canalOrigen, estado);
    }

    @GetMapping("/{id}")
    public Pedido obtener(@PathVariable Integer id) {
        Pedido pedido = service.obtener(id);
        verificarAccesoPedido(pedido);
        return pedido;
    }

    @GetMapping("/{id}/detalle")
    public PedidoDetalleResponse obtenerDetalle(@PathVariable Integer id) {
        Pedido pedido = service.obtener(id);
        verificarAccesoPedido(pedido);
        return service.obtenerDetalle(id);
    }

    @PostMapping
    public ResponseEntity<Pedido> crear(@Valid @RequestBody Pedido pedido) {
        SecurityUtils.requerirStaff();
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(pedido));
    }

    @PutMapping("/{id}")
    public Pedido actualizar(@PathVariable Integer id, @RequestBody Pedido pedido) {
        SecurityUtils.requerirStaff();
        return service.actualizar(id, pedido);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        SecurityUtils.requerirStaff();
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    private void verificarAccesoPedido(Pedido pedido) {
        if (pedido.getUsuario() == null) {
            SecurityUtils.requerirStaff();
            return;
        }
        SecurityUtils.requerirPedidoPropio(pedido.getUsuario().getIdUsuario());
    }
}
