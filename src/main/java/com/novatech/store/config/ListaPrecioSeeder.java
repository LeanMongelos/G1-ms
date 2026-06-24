package com.novatech.store.config;

import com.novatech.store.entity.ListaPrecio;
import com.novatech.store.repository.ListaPrecioRepository;
import com.novatech.store.util.ListaPrecioCodigo;
import java.math.BigDecimal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración Spring `ListaPrecioSeeder`: beans, seguridad, seeders o ajustes de arranque.
 */
@Configuration
public class ListaPrecioSeeder {

    @Bean
    CommandLineRunner seedListasPrecio(ListaPrecioRepository repo) {
        return args -> {
            crearSiNoExiste(repo, ListaPrecioCodigo.MAYORISTA,
                    "Mayorista / Revendedor",
                    "Mejor precio para revendedores y compras por volumen.",
                    new BigDecimal("25.00"));
            crearSiNoExiste(repo, ListaPrecioCodigo.B2B,
                    "B2B / Empresa",
                    "Precios para empresas, instituciones y ventas asistidas (CRM, admin).",
                    new BigDecimal("18.00"));
            crearSiNoExiste(repo, ListaPrecioCodigo.ECOMMERCE,
                    "E-commerce",
                    "Tienda online — precio público web.",
                    new BigDecimal("10.00"));
            crearSiNoExiste(repo, ListaPrecioCodigo.LOCAL,
                    "Local / POS",
                    "Mostrador físico — precio de venta en sucursal.",
                    new BigDecimal("5.00"));
        };
    }

    private void crearSiNoExiste(ListaPrecioRepository repo, String codigo,
                                 String nombre, String descripcion, BigDecimal descuentoGlobal) {
        repo.findByCodigoIgnoreCase(codigo).ifPresentOrElse(existing -> {
            if (existing.getDescuentoGlobal() == null) {
                existing.setDescuentoGlobal(descuentoGlobal);
                repo.save(existing);
            }
        }, () -> {
            ListaPrecio l = new ListaPrecio();
            l.setCodigo(codigo);
            l.setNombre(nombre);
            l.setDescripcion(descripcion);
            l.setDescuentoGlobal(descuentoGlobal);
            l.setActivo(true);
            repo.save(l);
        });
    }
}
