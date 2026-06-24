package com.novatech.store.service;

import com.novatech.store.entity.Permiso;
import com.novatech.store.entity.RolPermiso;
import com.novatech.store.entity.RolRbac;
import com.novatech.store.repository.PermisoRepository;
import com.novatech.store.repository.RolPermisoRepository;
import com.novatech.store.repository.RolRbacRepository;
import com.novatech.store.repository.UsuarioRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Servicio `RbacService`: reglas de negocio, transacciones y orquestación de Rbac. Los controllers delegan aquí; no accede HTTP directamente.
 */
@Service
public class RbacService {

    private final PermisoRepository permisoRepository;
    private final RolPermisoRepository rolPermisoRepository;
    private final RolRbacRepository rolRbacRepository;
    private final UsuarioRepository usuarioRepository;

    public RbacService(
            PermisoRepository permisoRepository,
            RolPermisoRepository rolPermisoRepository,
            RolRbacRepository rolRbacRepository,
            UsuarioRepository usuarioRepository) {
        this.permisoRepository = permisoRepository;
        this.rolPermisoRepository = rolPermisoRepository;
        this.rolRbacRepository = rolRbacRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Map<String, Object> matriz() {
        seedSiVacio();
        List<RolRbac> roles = rolRbacRepository.findAllByOrderByClaveAsc();
        List<Permiso> permisos = permisoRepository.findAllByOrderByModuloAscClaveAsc();
        Map<String, List<String>> asignaciones = new LinkedHashMap<>();
        for (RolRbac rol : roles) {
            if (Boolean.TRUE.equals(rol.getAccesoTotal())) {
                asignaciones.put(rol.getClave(), List.of("*"));
            } else {
                List<String> lista = rolPermisoRepository.findByRolClave(rol.getClave()).stream()
                        .map(RolPermiso::getPermisoClave)
                        .sorted()
                        .toList();
                asignaciones.put(rol.getClave(), lista);
            }
        }
        Map<String, Object> out = new HashMap<>();
        out.put("roles", roles);
        out.put("permisos", permisos);
        out.put("asignaciones", asignaciones);
        return out;
    }

    public List<RolRbac> listarRoles() {
        seedSiVacio();
        return rolRbacRepository.findAllByOrderByClaveAsc();
    }

    @Transactional
    public RolRbac crearRol(String clave, String nombre, String descripcion) {
        seedSiVacio();
        String key = normalizarClave(clave);
        if (key.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La clave del rol es obligatoria");
        }
        if (rolRbacRepository.existsByClaveIgnoreCase(key)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un rol con esa clave");
        }
        RolRbac rol = new RolRbac();
        rol.setClave(key);
        rol.setNombre(nombre != null && !nombre.isBlank() ? nombre.trim() : key);
        rol.setDescripcion(descripcion != null ? descripcion.trim() : "");
        rol.setEsSistema(false);
        rol.setAccesoTotal(false);
        rol.setAccesoPanel(true);
        return rolRbacRepository.save(rol);
    }

    @Transactional
    public RolRbac actualizarRolMeta(String clave, String nombre, String descripcion) {
        RolRbac rol = obtenerRol(clave);
        if (nombre != null && !nombre.isBlank()) {
            rol.setNombre(nombre.trim());
        }
        if (descripcion != null) {
            rol.setDescripcion(descripcion.trim());
        }
        return rolRbacRepository.save(rol);
    }

    @Transactional
    public void actualizarPermisosRol(String rolClave, List<String> permisos) {
        RolRbac rol = obtenerRol(rolClave);
        if (Boolean.TRUE.equals(rol.getAccesoTotal())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El rol " + rol.getClave() + " tiene acceso total y no se edita por permiso");
        }
        rolPermisoRepository.deleteByRolClave(rol.getClave());
        Set<String> validos = new HashSet<>();
        permisoRepository.findAll().forEach(p -> validos.add(p.getClave()));
        for (String p : permisos) {
            if (validos.contains(p)) {
                guardarPermiso(rol.getClave(), p);
            }
        }
    }

    @Transactional
    public void eliminarRol(String clave) {
        RolRbac rol = obtenerRol(clave);
        if (Boolean.TRUE.equals(rol.getEsSistema())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "No se puede eliminar un rol del sistema");
        }
        long usuarios = usuarioRepository.countByRolIgnoreCase(rol.getClave());
        if (usuarios > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Hay " + usuarios + " usuario(s) con este rol. Reasignálos antes de eliminar.");
        }
        rolPermisoRepository.deleteByRolClave(rol.getClave());
        rolRbacRepository.delete(rol);
    }

    public boolean tieneAccesoPanel(String rolClave) {
        if (rolClave == null || rolClave.isBlank()) {
            return false;
        }
        seedSiVacio();
        return rolRbacRepository.findByClaveIgnoreCase(rolClave)
                .map(r -> Boolean.TRUE.equals(r.getAccesoPanel()) || Boolean.TRUE.equals(r.getAccesoTotal()))
                .orElse("ADMIN".equalsIgnoreCase(rolClave) || "SUPERADMIN".equalsIgnoreCase(rolClave));
    }

    public boolean tieneAccesoTotal(String rolClave) {
        if (rolClave == null) {
            return false;
        }
        if ("SUPERADMIN".equalsIgnoreCase(rolClave) || "ADMIN".equalsIgnoreCase(rolClave)) {
            return true;
        }
        return rolRbacRepository.findByClaveIgnoreCase(rolClave)
                .map(r -> Boolean.TRUE.equals(r.getAccesoTotal()))
                .orElse(false);
    }

    public List<String> permisosDeRol(String rolClave) {
        if (tieneAccesoTotal(rolClave)) {
            return permisoRepository.findAllByOrderByModuloAscClaveAsc().stream()
                    .map(Permiso::getClave)
                    .toList();
        }
        return rolPermisoRepository.findByRolClave(rolClave).stream()
                .map(RolPermiso::getPermisoClave)
                .toList();
    }

    @Transactional
    public void seedSiVacio() {
        seedPermisos();
        seedPermisosCrm();
        seedPermisosOperaciones();
        seedRoles();
    }

    /** Agrega permisos operativos (pedidos, productos, facturación) si la BD ya existía. */
    private void seedPermisosOperaciones() {
        asegurarPermiso("pedidos.read", "pedidos", "Ver pedidos");
        asegurarPermiso("pedidos.create", "pedidos", "Crear pedidos");
        asegurarPermiso("pedidos.update", "pedidos", "Editar pedidos");
        asegurarPermiso("productos.read", "productos", "Ver productos");
        asegurarPermiso("productos.create", "productos", "Crear productos");
        asegurarPermiso("productos.update", "productos", "Editar productos");
        asegurarPermiso("facturacion.read", "facturacion", "Ver facturación");
        asegurarPermiso("facturacion.create", "facturacion", "Emitir comprobantes");
        asegurarPermiso("pagos.read", "pagos", "Ver pagos");
        asegurarPermiso("pagos.approve", "pagos", "Aprobar pagos pendientes");
        asegurarPermiso("envios.read", "envios", "Ver envíos");
        asegurarPermiso("envios.update", "envios", "Actualizar envíos");
        asignarPermisosSiFaltan("VENDEDOR", List.of(
                "pedidos.read", "pedidos.create", "pedidos.update",
                "productos.read", "pagos.read", "pagos.approve",
                "envios.read", "facturacion.read"));
    }

    /** Agrega permisos CRM/clientes si la BD ya tenía permisos previos. */
    private void seedPermisosCrm() {
        asegurarPermiso("clientes.read", "clientes", "Ver clientes");
        asegurarPermiso("clientes.create", "clientes", "Crear clientes");
        asegurarPermiso("clientes.update", "clientes", "Editar clientes");
        asegurarPermiso("clientes.deactivate", "clientes", "Dar de baja clientes");
        asegurarPermiso("clientes.export", "clientes", "Exportar clientes");
        asegurarPermiso("crm.read", "crm", "Ver bandeja CRM");
        asegurarPermiso("crm.reply", "crm", "Responder conversaciones");
        asegurarPermiso("crm.assign", "crm", "Asignar conversaciones");
        asegurarPermiso("crm.manage_channels", "crm", "Gestionar canales");
        asignarPermisosSiFaltan("GERENTE", List.of(
                "clientes.read", "clientes.create", "clientes.update", "clientes.deactivate", "clientes.export",
                "crm.read", "crm.reply", "crm.assign", "crm.manage_channels"));
        asignarPermisosSiFaltan("VENDEDOR", List.of(
                "clientes.read", "clientes.create", "clientes.update",
                "crm.read", "crm.reply"));
    }

    private void asegurarPermiso(String clave, String modulo, String desc) {
        if (permisoRepository.findByClaveIgnoreCase(clave).isEmpty()) {
            permisoRepository.save(perm(clave, modulo, desc));
        }
    }

    private void seedPermisos() {
        if (permisoRepository.count() > 0) {
            return;
        }
        List<Permiso> lista = new ArrayList<>();
        lista.add(perm("usuarios.read", "usuarios", "Ver usuarios"));
        lista.add(perm("usuarios.create", "usuarios", "Crear usuarios"));
        lista.add(perm("usuarios.update", "usuarios", "Editar usuarios"));
        lista.add(perm("usuarios.deactivate", "usuarios", "Activar/desactivar usuarios"));
        lista.add(perm("usuarios.assign_roles", "usuarios", "Asignar roles y permisos"));
        lista.add(perm("emisores.read", "emisores", "Ver emisores"));
        lista.add(perm("emisores.create", "emisores", "Crear emisores"));
        lista.add(perm("emisores.update", "emisores", "Editar emisores"));
        lista.add(perm("emisores.delete", "emisores", "Eliminar emisores"));
        lista.add(perm("config.read", "config", "Ver configuración"));
        lista.add(perm("config.update", "config", "Editar configuración"));
        lista.add(perm("config.manage_accounting", "config", "Gestionar contabilidad"));
        lista.add(perm("config.manage_integrations", "config", "Gestionar integraciones"));
        lista.add(perm("config.manage_billing_templates", "config", "Gestionar plantillas"));
        lista.add(perm("auditoria.read", "auditoria", "Ver auditoría"));
        lista.add(perm("logs.read", "logs", "Ver logs del sistema"));
        lista.add(perm("pedidos.read", "pedidos", "Ver pedidos"));
        lista.add(perm("pedidos.create", "pedidos", "Crear pedidos"));
        lista.add(perm("pedidos.update", "pedidos", "Editar pedidos"));
        lista.add(perm("productos.read", "productos", "Ver productos"));
        lista.add(perm("productos.create", "productos", "Crear productos"));
        lista.add(perm("productos.update", "productos", "Editar productos"));
        lista.add(perm("facturacion.read", "facturacion", "Ver facturación"));
        lista.add(perm("facturacion.create", "facturacion", "Emitir comprobantes"));
        lista.add(perm("pagos.read", "pagos", "Ver pagos"));
        lista.add(perm("pagos.approve", "pagos", "Aprobar pagos pendientes"));
        lista.add(perm("envios.read", "envios", "Ver envíos"));
        lista.add(perm("envios.update", "envios", "Actualizar envíos"));
        permisoRepository.saveAll(lista);
    }

    /** Idempotente: crea roles del sistema y permisos faltantes sin duplicar filas. */
    private void seedRoles() {
        asegurarRolSistema("SUPERADMIN", "Superadministrador",
                "Acceso total al sistema. Puede crear roles y asignar permisos.", true, true);
        asegurarRolSistema("GERENTE", "Gerente",
                "Gestión operativa, configuración y auditoría.", false, true);
        asegurarRolSistema("VENDEDOR", "Vendedor",
                "CRM, pedidos y catálogo sin acceso a configuración crítica.", false, true);
        asegurarRolSistema("CLIENTE", "Cliente",
                "Usuario de la tienda online.", false, false);

        List<String> todos = permisoRepository.findAllByOrderByModuloAscClaveAsc().stream()
                .map(Permiso::getClave)
                .toList();
        asignarPermisosSiFaltan("GERENTE", todos);
        asignarPermisosSiFaltan("VENDEDOR", List.of(
                "usuarios.read", "config.read",
                "clientes.read", "clientes.create", "clientes.update",
                "crm.read", "crm.reply",
                "pedidos.read", "pedidos.create", "pedidos.update",
                "productos.read",
                "pagos.read", "pagos.approve",
                "envios.read", "facturacion.read"));
    }

    private void asegurarRolSistema(String clave, String nombre, String desc,
                                    boolean accesoTotal, boolean accesoPanel) {
        if (rolRbacRepository.existsByClaveIgnoreCase(clave)) {
            return;
        }
        RolRbac rol = new RolRbac();
        rol.setClave(clave);
        rol.setNombre(nombre);
        rol.setDescripcion(desc);
        rol.setEsSistema(true);
        rol.setAccesoTotal(accesoTotal);
        rol.setAccesoPanel(accesoPanel);
        rolRbacRepository.save(rol);
    }

    private void asignarPermisosSiFaltan(String rolClave, List<String> permisos) {
        Set<String> actuales = new HashSet<>();
        rolPermisoRepository.findByRolClave(rolClave).forEach(rp -> actuales.add(rp.getPermisoClave()));
        for (String p : permisos) {
            if (!actuales.contains(p)) {
                guardarPermiso(rolClave, p);
            }
        }
    }

    private void guardarPermiso(String rolClave, String permisoClave) {
        RolPermiso rp = new RolPermiso();
        rp.setRolClave(rolClave);
        rp.setPermisoClave(permisoClave);
        rolPermisoRepository.save(rp);
    }

    private RolRbac obtenerRol(String clave) {
        seedSiVacio();
        return rolRbacRepository.findByClaveIgnoreCase(clave)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rol no encontrado"));
    }

    private String normalizarClave(String clave) {
        return clave.trim().toUpperCase().replaceAll("[^A-Z0-9_]", "_");
    }

    private Permiso perm(String clave, String modulo, String desc) {
        Permiso p = new Permiso();
        p.setClave(clave);
        p.setModulo(modulo);
        p.setDescripcion(desc);
        return p;
    }
}
