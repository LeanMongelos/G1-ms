package com.novatech.store.controller;

import com.novatech.store.entity.Pago;
import com.novatech.store.service.PagoService;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Controller de pagos. Todas las rutas empiezan con /pagos.
@RestController
@RequestMapping("/pagos")
public class PagoController {

    private final PagoService service;

    public PagoController(PagoService service) {
        this.service = service;
    }

    // GET /pagos -> lista de pagos.
    @GetMapping
    public List<Pago> listar() {
        return service.listar();
    }

    // GET /pagos/5 -> un pago por id.
    @GetMapping("/{id}")
    public Pago obtener(@PathVariable Integer id) {
        return service.obtener(id);
    }

    // POST /pagos -> crea un pago.
    @PostMapping
    public ResponseEntity<Pago> crear(@RequestBody Pago pago) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(pago));
    }

    // GET /pagos/qr?idPedido=5&monto=1200 -> devuelve el "contenido" del QR a pagar.
    // Es un payload SIMULADO (texto) con el id del pedido y el monto, mas una
    // referencia inventada. El frontend lo usa para dibujar el codigo QR.
    // En una integracion real, este texto vendria de la pasarela (Mercado Pago, etc.).
    @GetMapping("/qr")
    public Map<String, Object> generarQr(@RequestParam Integer idPedido,
                                         @RequestParam Double monto) {
        // Referencia simulada (no es un id real de ninguna pasarela).
        String referencia = "QR-" + idPedido + "-" + System.currentTimeMillis();
        // Texto que se va a codificar dentro del QR.
        String payload = "NOVATECH|PEDIDO:" + idPedido + "|MONTO:" + monto + "|REF:" + referencia;
        return Map.of(
                "payload", payload,
                "referencia", referencia,
                "monto", monto,
                "idPedido", idPedido
        );
    }

    // PUT /pagos/5 -> actualiza un pago.
    @PutMapping("/{id}")
    public Pago actualizar(@PathVariable Integer id, @RequestBody Pago pago) {
        return service.actualizar(id, pago);
    }

    // DELETE /pagos/5 -> borra un pago.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
