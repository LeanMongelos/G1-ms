package com.novatech.store.config;

import com.novatech.store.entity.*;
import com.novatech.store.repository.*;
import java.time.LocalDateTime;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración Spring `CrmDemoSeeder`: beans, seguridad, seeders o ajustes de arranque.
 */
@Configuration
public class CrmDemoSeeder {

    @Bean
    CommandLineRunner seedCrmDemo(
            ConversacionRepository conversacionRepository,
            MensajeConversacionRepository mensajeRepository,
            IntegracionCanalRepository integracionRepository,
            ConfiguracionSistemaRepository configRepository,
            RegistroAuditoriaRepository auditoriaRepository,
            LogSistemaRepository logRepository,
            UsuarioRepository usuarioRepository,
            PerfilClienteRepository perfilRepository) {
        return args -> {
            seedIntegraciones(integracionRepository);
            seedConfiguracion(configRepository);
            seedAuditoria(auditoriaRepository);
            seedLogs(logRepository);
            Usuario admin = usuarioRepository.findAll().stream()
                    .filter(u -> "SUPERADMIN".equalsIgnoreCase(u.getRol())
                            || "ADMIN".equalsIgnoreCase(u.getRol()))
                    .findFirst()
                    .orElse(null);
            PerfilCliente perfil = perfilRepository.findAll().stream().findFirst().orElse(null);
            sincronizarDemoBandeja(configRepository, conversacionRepository, mensajeRepository, admin, perfil);
        };
    }

    private static final String DEMO_BANDEJA_VERSION = "4";

    /** Crea o actualiza las 4 conversaciones demo del retail tech NovaTech Store. */
    private void sincronizarDemoBandeja(
            ConfiguracionSistemaRepository configRepository,
            ConversacionRepository conversacionRepository,
            MensajeConversacionRepository mensajeRepository,
            Usuario admin,
            PerfilCliente perfil) {

        upsertConfig(configRepository, "crm", "etiquetas_conversacion",
                "venta,soporte,reclamo,presupuesto,urgente,stock,garantia,consulta,mayorista,envio",
                "Etiquetas bandeja CRM");

        String actual = configRepository.findByClaveIgnoreCase("demo_bandeja_version")
                .map(ConfiguracionSistema::getValor).orElse("");
        if (DEMO_BANDEJA_VERSION.equals(actual) && conversacionRepository.count() >= 4) {
            return;
        }

        upsertDemoCanal(conversacionRepository, mensajeRepository, admin, perfil, "EMAIL",
                "Compras — Oficina Tech Solutions", "compras@oficinatech.com.ar", null,
                "Presupuesto 10 notebooks Lenovo ThinkPad",
                "Necesitamos cotización formal para equipar oficina…",
                "PENDIENTE", "presupuesto,venta", null,
                new String[][]{
                        {"SALIENTE", "NovaTech Store", "Recibimos su consulta. ¿Prefieren ThinkPad E14 Gen 5 o T14 con 16 GB RAM?", "2026-06-19T10:30"},
                        {"ENTRANTE", "Lic. Patricia Morales", "Buenos días, necesitamos 10 notebooks Lenovo ThinkPad para área administrativa, SSD 512 GB y Windows Pro. ¿Tienen stock y plazo de entrega?", "2026-06-20T09:15"}
                });

        upsertDemoCanal(conversacionRepository, mensajeRepository, admin, perfil, "FACEBOOK",
                "Marcos G. — Gamer", null, null,
                "Consulta RTX 4070 y compatibilidad",
                "¿Tienen GPU RTX 4070 en stock? Mi fuente es 650W…",
                "PENDIENTE", "consulta,stock,venta", perfil,
                new String[][]{
                        {"ENTRANTE", "Marcos G.", "Hola NovaTech, ¿tienen RTX 4070 Super en stock? Tengo Ryzen 5 5600X y fuente Thermaltake 650W 80+ Bronze. ¿Es compatible?", "2026-06-20T14:00"}
                });

        upsertDemoCanal(conversacionRepository, mensajeRepository, admin, perfil, "INSTAGRAM",
                "Studio Creativo BA", null, null,
                "Monitor ultrawide 34\" para diseño",
                "Busco monitor IPS 3440×1440 con buena calibración…",
                "EN_PROCESO", "consulta,venta", null,
                new String[][]{
                        {"ENTRANTE", "Valentina Ruiz", "Hola, busco monitor ultrawide 34\" IPS para diseño gráfico (3440×1440). ¿Recomiendan el LG 34WP65C o tienen otra opción con USB-C?", "2026-06-19T16:45"}
                });

        upsertDemoCanal(conversacionRepository, mensajeRepository, admin, perfil, "WHATSAPP",
                "Informática Norte — Mayorista", null, "+543764123456",
                "Combo teclado + mouse Logitech mayorista",
                "Consulta por 50 combos MK270 para reventa…",
                "PENDIENTE", "mayorista,presupuesto,stock", null,
                new String[][]{
                        {"ENTRANTE", "Carlos Méndez", "Buen día, somos revendedores en Misiones. Necesitamos 50 combos teclado + mouse Logitech MK270 con precio mayorista y envío a Posadas. ¿Tienen lista B2B?", "2026-06-20T11:20"}
                });

        upsertConfig(configRepository, "crm", "demo_bandeja_version", DEMO_BANDEJA_VERSION,
                "Versión datos demo bandeja CRM");
    }

    private void upsertDemoCanal(
            ConversacionRepository convRepo,
            MensajeConversacionRepository msgRepo,
            Usuario admin,
            PerfilCliente perfil,
            String canal,
            String nombre,
            String email,
            String tel,
            String asunto,
            String preview,
            String estado,
            String tags,
            PerfilCliente cliente,
            String[][] mensajes) {

        Conversacion conv = convRepo.findByCanalIgnoreCase(canal).stream().findFirst().orElse(null);
        if (conv == null) {
            conv = crearConv(canal, nombre, email, tel, asunto, preview, estado, tags, admin, cliente);
            conv = convRepo.save(conv);
        } else {
            conv.setContactoNombre(nombre);
            conv.setContactoEmail(email);
            conv.setContactoTelefono(tel);
            conv.setAsunto(asunto);
            conv.setVistaPrevia(preview.length() > 80 ? preview.substring(0, 80) + "…" : preview);
            conv.setEstado(estado);
            conv.setEtiquetas(tags);
            conv.setAsignadoA(admin);
            if (cliente != null) {
                conv.setCliente(cliente);
            }
            conv.setUltimaActividad(LocalDateTime.now());
            conv = convRepo.save(conv);
            msgRepo.findByConversacionIdConversacionOrderByFechaAsc(conv.getIdConversacion())
                    .forEach(msgRepo::delete);
        }
        for (String[] m : mensajes) {
            guardarMensaje(msgRepo, conv, m[0], m[1], m[2], LocalDateTime.parse(m[3]));
        }
    }

    private void upsertConfig(ConfiguracionSistemaRepository repo, String grupo, String clave,
                              String valor, String desc) {
        repo.findByClaveIgnoreCase(clave).ifPresentOrElse(c -> {
            c.setValor(valor);
            c.setDescripcion(desc);
            repo.save(c);
        }, () -> guardarConfig(repo, grupo, clave, valor, desc));
    }

    private void seedIntegraciones(IntegracionCanalRepository repo) {
        crearIntegracion(repo, "WHATSAPP", "WhatsApp Business API", true, "CONECTADO",
                "{\"phoneNumberId\":\"demo\",\"token\":\"***\"}");
        crearIntegracion(repo, "INSTAGRAM", "Instagram Direct", true, "CONECTADO", "{}");
        crearIntegracion(repo, "FACEBOOK", "Facebook Messenger", true, "DESCONECTADO", "{}");
        crearIntegracion(repo, "EMAIL", "Correo IMAP/SMTP", true, "CONECTADO",
                "{\"imapHost\":\"mail.novatech.com\",\"smtpHost\":\"mail.novatech.com\"}");
        crearIntegracion(repo, "EMAIL_GRAPH", "Microsoft Graph (365)", false, "PENDIENTE", "{}");
        crearIntegracion(repo, "N8N", "Automatización n8n", false, "DESCONECTADO",
                "{\"webhookUrl\":\"\"}");
    }

    private void crearIntegracion(IntegracionCanalRepository repo, String tipo, String nombre,
                                  boolean activo, String estado, String config) {
        if (repo.findByTipoIgnoreCase(tipo).isEmpty()) {
            IntegracionCanal i = new IntegracionCanal();
            i.setTipo(tipo);
            i.setNombre(nombre);
            i.setActivo(activo);
            i.setEstadoConexion(estado);
            i.setConfigJson(config);
            repo.save(i);
        }
    }

    private void seedConfiguracion(ConfiguracionSistemaRepository repo) {
        guardarConfig(repo, "contabilidad", "iva_general", "21", "Alícuota IVA general");
        guardarConfig(repo, "contabilidad", "iibb", "3.5", "Ingresos brutos %");
        guardarConfig(repo, "contabilidad", "retenciones_activas", "true", "Retenciones habilitadas");
        guardarConfig(repo, "afip", "cuit_emisor", "30-71234567-8", "CUIT emisor principal");
        guardarConfig(repo, "afip", "punto_venta", "0001", "Punto de venta AFIP");
        guardarConfig(repo, "afip", "certificado_vencimiento", "2026-12-31", "Vencimiento certificado");
        guardarConfig(repo, "plantillas", "factura_encabezado", "NovaTech Store S.A.", "Encabezado factura");
        guardarConfig(repo, "plantillas", "presupuesto_pie", "Validez 15 días", "Pie presupuesto");
        guardarConfig(repo, "notificaciones", "email_alertas", "true", "Alertas por email");
        guardarConfig(repo, "notificaciones", "whatsapp_alertas", "false", "Alertas WhatsApp");
        guardarConfig(repo, "seguridad", "2fa_obligatorio", "false", "2FA obligatorio para admins");
        guardarConfig(repo, "contabilidad", "plan_cuentas_codigo", "AR-PCG-01", "Plan de cuentas");
        guardarConfig(repo, "contabilidad", "moneda_base", "ARS", "Moneda base");
        guardarConfig(repo, "afip", "razon_social", "NovaTech Store S.A.", "Razón social");
        guardarConfig(repo, "plantillas", "factura_pie", "Gracias por su compra.", "Pie factura");
        guardarConfig(repo, "plantillas", "presupuesto_encabezado", "Presupuesto comercial", "Encabezado presupuesto");
        guardarConfig(repo, "plantillas", "remito_encabezado", "Remito de entrega", "Encabezado remito");
        guardarConfig(repo, "notificaciones", "email_remitente", "alertas@novatech.com", "Email remitente");
        guardarConfig(repo, "notificaciones", "dias_antes_vencimiento_cuota", "3", "Días antes de vencimiento");
        guardarConfig(repo, "seguridad", "intentos_login_max", "5", "Intentos login");
        guardarConfig(repo, "auditoria", "retencion_dias", "365", "Retención auditoría");
        guardarConfig(repo, "logs", "retencion_dias", "15", "Retención logs técnicos");
    }

    private void guardarConfig(ConfiguracionSistemaRepository repo, String grupo, String clave,
                               String valor, String desc) {
        if (repo.findByClaveIgnoreCase(clave).isEmpty()) {
            ConfiguracionSistema c = new ConfiguracionSistema();
            c.setGrupo(grupo);
            c.setClave(clave);
            c.setValor(valor);
            c.setDescripcion(desc);
            repo.save(c);
        }
    }

    private Conversacion crearConv(String canal, String nombre, String email, String tel,
                                   String asunto, String preview, String estado, String tags,
                                   Usuario asignado, PerfilCliente cliente) {
        Conversacion c = new Conversacion();
        c.setCanal(canal);
        c.setContactoNombre(nombre);
        c.setContactoEmail(email);
        c.setContactoTelefono(tel);
        c.setAsunto(asunto);
        c.setVistaPrevia(preview);
        c.setEstado(estado);
        c.setEtiquetas(tags);
        c.setAsignadoA(asignado);
        c.setCliente(cliente);
        c.setFechaCreacion(LocalDateTime.now().minusDays(2));
        c.setUltimaActividad(LocalDateTime.now());
        return c;
    }

    private void guardarMensaje(MensajeConversacionRepository repo, Conversacion conv,
                                String dir, String remitente, String cuerpo, LocalDateTime fecha) {
        MensajeConversacion m = new MensajeConversacion();
        m.setConversacion(conv);
        m.setDireccion(dir);
        m.setRemitenteNombre(remitente);
        m.setCuerpo(cuerpo);
        m.setFecha(fecha);
        repo.save(m);
    }

    private void seedAuditoria(RegistroAuditoriaRepository repo) {
        if (repo.count() > 0) {
            return;
        }
        crearAudit(repo, "Leandro Mongelos", "Usuarios", "Alta", "Usuario vendedor creado: jperez@novatech.com",
                "Usuario", "12", "192.168.1.10", null, "{\"rol\":\"CLIENTE\"}",
                LocalDateTime.now().minusHours(2));
        crearAudit(repo, "Leandro Mongelos", "Facturación", "Emisión", "Factura FA-00001234 emitida",
                "Factura", "1234", "192.168.1.10", null, "{\"estado\":\"EMITIDA\"}",
                LocalDateTime.now().minusHours(5));
        crearAudit(repo, "Guillermo Aquiles", "CRM", "Asignación", "Conversación #3 asignada a Guillermo Aquiles",
                "Conversacion", "3", null, null, null,
                LocalDateTime.now().minusHours(8));
        crearAudit(repo, "Leandro Mongelos", "Integraciones", "Conexión", "Canal WhatsApp conectado",
                "IntegracionCanal", "1", null, null, "{\"estado\":\"CONECTADO\"}",
                LocalDateTime.now().minusDays(1));
        crearAudit(repo, "Leandro Mongelos", "Productos", "Actualización", "Stock mínimo actualizado en 12 productos",
                "Producto", null, null, null, null,
                LocalDateTime.now().minusDays(2));
    }

    private void crearAudit(RegistroAuditoriaRepository repo, String usuario, String modulo,
                            String accion, String detalle, String entidad, String entidadId, String ip,
                            String antes, String despues, LocalDateTime fecha) {
        RegistroAuditoria r = new RegistroAuditoria();
        r.setUsuarioNombre(usuario);
        r.setModulo(modulo);
        r.setAccion(accion);
        r.setDetalle(detalle);
        r.setEntidad(entidad);
        r.setEntidadId(entidadId);
        r.setIp(ip);
        r.setDatosAntes(antes);
        r.setDatosDespues(despues);
        r.setFecha(fecha);
        repo.save(r);
    }

    private void seedLogs(LogSistemaRepository repo) {
        if (repo.count() > 0) {
            return;
        }
        crearLog(repo, "WARN", "IntegracionCanalService", "Facebook Messenger desconectado: token expirado",
                LocalDateTime.now().minusMinutes(45));
        crearLog(repo, "ERROR", "EmailSyncJob", "IMAP timeout al sincronizar bandeja de entrada",
                "java.net.SocketTimeoutException: connect timed out\n\tat EmailSyncJob.run(EmailSyncJob.java:42)",
                "{\"host\":\"imap.example.com\",\"timeoutMs\":30000}",
                LocalDateTime.now().minusHours(3));
        crearLog(repo, "INFO", "AutomatizacionScheduler", "Campaña programada enviada: 128 destinatarios",
                LocalDateTime.now().minusHours(6));
        crearLog(repo, "ERROR", "FacturaService", "AFIP rechazó comprobante: punto de venta inválido",
                "com.novatech.store.AfipException: Punto de venta 9999 no habilitado",
                "{\"puntoVenta\":9999,\"cuit\":\"30-71234567-8\"}",
                LocalDateTime.now().minusDays(1));
        crearLog(repo, "WARN", "OrdenCompraService", "Proveedor sin respuesta en OC #7",
                LocalDateTime.now().minusDays(3));
    }

    private void crearLog(LogSistemaRepository repo, String nivel, String origen,
                          String mensaje, LocalDateTime fecha) {
        crearLog(repo, nivel, origen, mensaje, null, null, fecha);
    }

    private void crearLog(LogSistemaRepository repo, String nivel, String origen,
                          String mensaje, String stack, String metadata, LocalDateTime fecha) {
        LogSistema l = new LogSistema();
        l.setNivel(nivel);
        l.setOrigen(origen);
        l.setMensaje(mensaje);
        l.setStackTrace(stack);
        l.setMetadataJson(metadata);
        l.setFecha(fecha);
        repo.save(l);
    }
}
