package com.novatech.store.security;

import com.novatech.store.dto.UsuarioResponse;
import java.util.Set;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private static final Set<String> STAFF_ROLES = Set.of(
            "SUPERADMIN", "ADMIN", "GERENTE", "VENDEDOR", "EMPLEADO",
            "CONTADOR", "LOGISTICA", "CAJERO", "COMPRAS", "MARKETING");

    private SecurityUtils() {
    }

    public static boolean esStaff(String rol) {
        return rol != null && STAFF_ROLES.contains(rol.trim().toUpperCase());
    }

    public static UsuarioResponse usuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UsuarioResponse usuario)) {
            return null;
        }
        return usuario;
    }

    public static UsuarioResponse requerirAutenticado() {
        UsuarioResponse u = usuarioActual();
        if (u == null) {
            throw new AccessDeniedException("Sesión requerida");
        }
        return u;
    }

    public static boolean esCliente(String rol) {
        return rol != null && "CLIENTE".equalsIgnoreCase(rol.trim());
    }

    public static void requerirCliente() {
        UsuarioResponse u = requerirAutenticado();
        if (!esCliente(u.rol())) {
            throw new AccessDeniedException("El portal de cliente es solo para usuarios con rol CLIENTE");
        }
    }

    public static void requerirStaff() {
        UsuarioResponse u = requerirAutenticado();
        if (!esStaff(u.rol())) {
            throw new AccessDeniedException("Acceso restringido al personal autorizado");
        }
    }

    public static void requerirPropioUsuario(Integer idUsuario) {
        UsuarioResponse u = requerirAutenticado();
        if (esStaff(u.rol())) {
            return;
        }
        if (idUsuario == null || !idUsuario.equals(u.idUsuario())) {
            throw new AccessDeniedException("No podés acceder a datos de otro usuario");
        }
    }

    public static void requerirPedidoPropio(Integer idUsuarioPedido) {
        requerirPropioUsuario(idUsuarioPedido);
    }
}
