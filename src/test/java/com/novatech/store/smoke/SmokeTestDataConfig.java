package com.novatech.store.smoke;

import com.novatech.store.entity.Carrito;
import com.novatech.store.entity.Categoria;
import com.novatech.store.entity.Cuota;
import com.novatech.store.entity.DetalleCarrito;
import com.novatech.store.entity.DetallePedido;
import com.novatech.store.entity.Envio;
import com.novatech.store.entity.Pago;
import com.novatech.store.entity.Pedido;
import com.novatech.store.entity.PerfilCliente;
import com.novatech.store.entity.PlanCuotas;
import com.novatech.store.entity.Producto;
import com.novatech.store.entity.Resena;
import com.novatech.store.entity.Usuario;
import com.novatech.store.repository.CarritoRepository;
import com.novatech.store.repository.CategoriaRepository;
import com.novatech.store.repository.CuotaRepository;
import com.novatech.store.repository.DetalleCarritoRepository;
import com.novatech.store.repository.DetallePedidoRepository;
import com.novatech.store.repository.EnvioRepository;
import com.novatech.store.repository.PagoRepository;
import com.novatech.store.repository.PedidoRepository;
import com.novatech.store.repository.PerfilClienteRepository;
import com.novatech.store.repository.PlanCuotasRepository;
import com.novatech.store.repository.ProductoRepository;
import com.novatech.store.repository.ResenaRepository;
import com.novatech.store.repository.UsuarioRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/** Programmatic seed for H2 smoke tests (avoids MySQL-specific data.sql). */
@Configuration
@Profile("test")
public class SmokeTestDataConfig {

    private static final String STAFF_HASH =
            "$2a$10$5/A7KT9NtU4SAMeujmt83.dCGMni9DDkrzH9mY5CAwKsIiRc9d/S2";
    private static final String CLIENTE_HASH =
            "$2a$10$dxxZcXFu1jsKxgfTxpxCe.HkPXphC5Jm4K9p15yA2RQ7aPfYkjgiS";

    @Bean
    CommandLineRunner smokeTestSeed(
            UsuarioRepository usuarioRepository,
            CategoriaRepository categoriaRepository,
            ProductoRepository productoRepository,
            PerfilClienteRepository perfilClienteRepository,
            CarritoRepository carritoRepository,
            DetalleCarritoRepository detalleCarritoRepository,
            PedidoRepository pedidoRepository,
            DetallePedidoRepository detallePedidoRepository,
            PagoRepository pagoRepository,
            EnvioRepository envioRepository,
            ResenaRepository resenaRepository,
            PlanCuotasRepository planCuotasRepository,
            CuotaRepository cuotaRepository) {
        return args -> {
            if (usuarioRepository.count() > 0) {
                return;
            }

            Usuario staff = saveUser(usuarioRepository, "Super Admin NovaTech", "superadmin@novatech.com", STAFF_HASH, "SUPERADMIN");
            Usuario cliente = saveUser(usuarioRepository, "Cliente Demo", "cliente@novatech.com", CLIENTE_HASH, "CLIENTE");

            Categoria categoria = new Categoria();
            categoria.setNombre("Notebooks");
            categoria.setDescripcion("Computadoras portatiles");
            categoria = categoriaRepository.save(categoria);

            Producto teclado = saveProducto(productoRepository, "Teclado Mecanico Redragon", categoria, 45000, 30);
            saveProducto(productoRepository, "Notebook Lenovo IdeaPad", categoria, 750000, 12);

            PerfilCliente perfil = new PerfilCliente();
            perfil.setUsuario(cliente);
            perfil.setDireccion("Av. Siempre Viva 742");
            perfil.setTelefono("11-5555-1234");
            perfil.setHistorialCrediticio(700);
            perfil.setTipoCliente("CONSUMIDOR_FINAL");
            perfil.setActivo(true);
            perfil = perfilClienteRepository.save(perfil);

            Carrito carrito = new Carrito();
            carrito.setUsuario(cliente);
            carrito.setFechaCreacion(LocalDateTime.now());
            carrito = carritoRepository.save(carrito);

            DetalleCarrito dc = new DetalleCarrito();
            dc.setCarrito(carrito);
            dc.setProducto(teclado);
            dc.setCantidad(1);
            detalleCarritoRepository.save(dc);

            Pedido pedido = new Pedido();
            pedido.setUsuario(cliente);
            pedido.setFecha(LocalDateTime.now());
            pedido.setEstado("PAGADO");
            pedido.setTotal(new BigDecimal("89000.00"));
            pedido = pedidoRepository.save(pedido);

            DetallePedido dp1 = new DetallePedido();
            dp1.setPedido(pedido);
            dp1.setProducto(teclado);
            dp1.setCantidad(1);
            dp1.setPrecioUnitario(new BigDecimal("45000.00"));
            detallePedidoRepository.save(dp1);

            Pago pago = new Pago();
            pago.setPedido(pedido);
            pago.setFechaPago(LocalDateTime.now());
            pago.setMonto(new BigDecimal("89000.00"));
            pago.setMetodo("TARJETA");
            pago.setAprobadoPor(staff);
            pagoRepository.save(pago);

            Envio envio = new Envio();
            envio.setPedido(pedido);
            envio.setDireccionEnvio("Av. Siempre Viva 742");
            envio.setEmpresaLogistica("Andreani");
            envio.setEstadoEnvio("EN_CAMINO");
            envioRepository.save(envio);

            Resena resena = new Resena();
            resena.setProducto(teclado);
            resena.setUsuario(cliente);
            resena.setComentario("Excelente teclado");
            resena.setPuntuacion(5);
            resena.setFecha(LocalDateTime.now());
            resenaRepository.save(resena);

            PlanCuotas plan = new PlanCuotas();
            plan.setCliente(perfil);
            plan.setPedido(pedido);
            plan.setCantidadCuotas(3);
            plan.setInteres(new BigDecimal("15.00"));
            plan.setEstado("ACTIVO");
            plan = planCuotasRepository.save(plan);

            saveCuota(cuotaRepository, plan, 1, new BigDecimal("34116.67"), LocalDate.now().minusDays(30), "PAGADA");
            saveCuota(cuotaRepository, plan, 2, new BigDecimal("34116.67"), LocalDate.now().plusDays(5), "PENDIENTE");
        };
    }

    private static Usuario saveUser(UsuarioRepository repo, String nombre, String email, String hash, String rol) {
        Usuario u = new Usuario();
        u.setNombre(nombre);
        u.setEmail(email);
        u.setContrasena(hash);
        u.setRol(rol);
        u.setFechaRegistro(LocalDateTime.now());
        return repo.save(u);
    }

    private static Producto saveProducto(
            ProductoRepository repo, String nombre, Categoria categoria, int precio, int stock) {
        Producto p = new Producto();
        p.setNombre(nombre);
        p.setDescripcion("Producto demo smoke");
        p.setPrecio(new BigDecimal(precio));
        p.setStock(stock);
        p.setCategoria(categoria);
        p.setProveedor("NovaTech");
        return repo.save(p);
    }

    private static void saveCuota(
            CuotaRepository repo, PlanCuotas plan, int numero, BigDecimal monto, LocalDate vencimiento, String estado) {
        Cuota c = new Cuota();
        c.setPlan(plan);
        c.setNumeroCuota(numero);
        c.setMonto(monto);
        c.setFechaVencimiento(vencimiento);
        c.setEstado(estado);
        c.setAvisoVencimientoEnviado(false);
        repo.save(c);
    }
}
