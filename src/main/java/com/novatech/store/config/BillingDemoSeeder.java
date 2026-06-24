package com.novatech.store.config;

import com.novatech.store.dto.LineaComprobanteDto;
import com.novatech.store.dto.PresupuestoRequest;
import com.novatech.store.entity.PerfilCliente;
import com.novatech.store.entity.Producto;
import com.novatech.store.repository.PerfilClienteRepository;
import com.novatech.store.repository.PresupuestoRepository;
import com.novatech.store.repository.ProductoRepository;
import com.novatech.store.service.PresupuestoService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración Spring `BillingDemoSeeder`: beans, seguridad, seeders o ajustes de arranque.
 */
@Configuration
public class BillingDemoSeeder {

    @Bean
    CommandLineRunner seedBillingDemo(PresupuestoRepository presupuestoRepository,
                                      PresupuestoService presupuestoService,
                                      PerfilClienteRepository perfilRepository,
                                      ProductoRepository productoRepository) {
        return args -> {
            if (presupuestoRepository.count() > 0) {
                return;
            }
            PerfilCliente cliente = perfilRepository.findAll().stream().findFirst().orElse(null);
            List<Producto> productos = productoRepository.findAll();
            if (cliente == null || productos.isEmpty()) {
                return;
            }

            Producto producto = productos.get(0);
            PresupuestoRequest req = new PresupuestoRequest();
            req.setIdCliente(cliente.getIdCliente());
            req.setValidezHasta(LocalDate.now().plusDays(30));
            req.setNotas("Presupuesto demo — 10 notebooks ThinkPad (NovaTech ERP)");

            LineaComprobanteDto linea = new LineaComprobanteDto();
            linea.setIdProducto(producto.getIdProducto());
            linea.setCantidad(10);
            linea.setPrecioUnitario(producto.getPrecio());
            linea.setDescuentoPorcentaje(new BigDecimal("5"));
            req.setLineas(List.of(linea));

            var presupuesto = presupuestoService.crear(req);
            presupuestoService.cambiarEstado(presupuesto.getIdPresupuesto(), "APROBADO");
        };
    }
}
