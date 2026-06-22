package com.novatech.store.util;

/** Utilidades para distinguir empleados de la empresa vs clientes de la tienda. */
public final class UsuarioRolUtil {

    private UsuarioRolUtil() {
    }

    /**
     * Empleados: SUPERADMIN, ADMIN, GERENTE, VENDEDOR y cualquier rol con acceso al panel
     * (distinto de CLIENTE).
     */
    public static boolean esEmpleado(String rol) {
        if (rol == null || rol.isBlank()) {
            return false;
        }
        return !"CLIENTE".equalsIgnoreCase(rol.trim());
    }
}
