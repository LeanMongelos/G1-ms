package com.novatech.store.config;

import com.novatech.store.entity.*;
import com.novatech.store.repository.*;
import com.novatech.store.service.RbacService;
import java.time.LocalDate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración Spring `ConfigSeeder`: beans, seguridad, seeders o ajustes de arranque.
 */
@Configuration
public class ConfigSeeder {

    @Bean
    CommandLineRunner seedConfigModule(
            RbacService rbacService,
            UsuarioRepository usuarioRepository,
            EmisorRepository emisorRepository,
            PlantillaImpresionRepository plantillaRepository,
            CatalogoMaestroRepository catalogoRepository) {
        return args -> {
            rbacService.seedSiVacio();
            migrarSuperAdmin(usuarioRepository);
            seedEmisores(emisorRepository);
            seedPlantillas(plantillaRepository);
            migrarPlantillasLegacy(plantillaRepository);
            seedCatalogos(catalogoRepository);
        };
    }

    private void seedEmisores(EmisorRepository repo) {
        if (repo.count() > 0) {
            return;
        }
        Emisor e = new Emisor();
        e.setRazonSocial("NovaTech Store S.A.");
        e.setCuit("30-71234567-8");
        e.setCondicionIva("RESPONSABLE_INSCRIPTO");
        e.setIibb("901-234567-8");
        e.setDomicilio("Av. Corrientes 1234, CABA");
        e.setPuntoVenta(1);
        e.setAmbiente("HOMOLOGACION");
        e.setEsDefault(true);
        e.setCertificadoVencimiento(LocalDate.of(2027, 6, 1));
        repo.save(e);
    }

    private void seedPlantillas(PlantillaImpresionRepository repo) {
        if (repo.count() > 0) {
            return;
        }
        crear(repo, "FACTURA", "Factura A4 estándar", PlantillaTemplates.jsonFactura(), true);
        crear(repo, "PRESUPUESTO", "Presupuesto comercial", PlantillaTemplates.jsonPresupuesto(), true);
        crear(repo, "REMITO", "Remito de entrega", PlantillaTemplates.jsonRemito(), true);
    }

    private void migrarPlantillasLegacy(PlantillaImpresionRepository repo) {
        for (PlantillaImpresion p : repo.findAll()) {
            String json = p.getContenidoJson();
            if (json != null && json.contains("\"html\"")) {
                continue;
            }
            if (!Boolean.TRUE.equals(p.getEsDefault())) {
                continue;
            }
            String tipo = p.getTipo() != null ? p.getTipo().toUpperCase() : "";
            String nuevo = switch (tipo) {
                case "PRESUPUESTO" -> PlantillaTemplates.jsonPresupuesto();
                case "REMITO" -> PlantillaTemplates.jsonRemito();
                case "FACTURA" -> PlantillaTemplates.jsonFactura();
                default -> null;
            };
            if (nuevo != null) {
                p.setContenidoJson(nuevo);
                repo.save(p);
            }
        }
    }

    private void crear(PlantillaImpresionRepository repo, String tipo, String nombre, String json, boolean def) {
        PlantillaImpresion p = new PlantillaImpresion();
        p.setTipo(tipo);
        p.setNombre(nombre);
        p.setContenidoJson(json);
        p.setEsDefault(def);
        p.setActivo(true);
        repo.save(p);
    }

    private void seedCatalogos(CatalogoMaestroRepository repo) {
        if (repo.count() > 0) {
            return;
        }
        cat(repo, "CATEGORIA", "NOTE", "Notebooks", 1);
        cat(repo, "CATEGORIA", "PERI", "Periféricos", 2);
        cat(repo, "DEPOSITO", "CENT", "Depósito central", 1);
        cat(repo, "DEPOSITO", "SUC1", "Sucursal Norte", 2);
        cat(repo, "CONDICION_PAGO", "CONT", "Contado", 1);
        cat(repo, "CONDICION_PAGO", "30D", "30 días", 2);
    }

    private void cat(CatalogoMaestroRepository repo, String tipo, String codigo, String nombre, int orden) {
        CatalogoMaestro c = new CatalogoMaestro();
        c.setTipo(tipo);
        c.setCodigo(codigo);
        c.setNombre(nombre);
        c.setOrden(orden);
        c.setActivo(true);
        if ("CONDICION_PAGO".equals(tipo)) {
            c.setDatosJson("{\"diasPlazo\":\"" + ("CONT".equals(codigo) ? "0" : "30") + "\"}");
        }
        repo.save(c);
    }

    private void migrarSuperAdmin(UsuarioRepository usuarioRepository) {
        usuarioRepository.findByEmail("admin@novatech.com").ifPresent(u -> {
            if ("ADMIN".equalsIgnoreCase(u.getRol())) {
                u.setRol("SUPERADMIN");
                usuarioRepository.save(u);
            }
        });
    }
}
