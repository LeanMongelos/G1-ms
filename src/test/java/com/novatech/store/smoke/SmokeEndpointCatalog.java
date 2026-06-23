package com.novatech.store.smoke;

import java.util.List;

/** Catalog of every GET endpoint discovered under controller/. */
public final class SmokeEndpointCatalog {

    private SmokeEndpointCatalog() {
    }

    /** Status codes that are acceptable for smoke (never 500). */
    public static final int[] OK = {200};
    public static final int[] OK_OR_EMPTY = {200, 204};
    public static final int[] OK_OR_NOT_FOUND = {200, 404};
    public static final int[] OK_OR_FORBIDDEN = {200, 403};
    public static final int[] OK_OR_UNAUTHORIZED = {200, 401};

    public static List<SmokeEndpoint> allGetEndpoints() {
        return List.of(
                // --- Public / health ---
                SmokeEndpoint.get("ping", "/ping", SmokeAuth.AUTH, OK),
                SmokeEndpoint.get("actuator-health", "/actuator/health", SmokeAuth.NONE, OK),
                SmokeEndpoint.get("productos-list", "/productos", SmokeAuth.NONE, OK),
                SmokeEndpoint.get("productos-filter-nombre", "/productos?nombre=Lenovo", SmokeAuth.NONE, OK),
                SmokeEndpoint.get("productos-filter-categoria", "/productos?categoriaId=1", SmokeAuth.NONE, OK),
                SmokeEndpoint.get("productos-filter-lista", "/productos?listaPrecio=ECOMMERCE", SmokeAuth.NONE, OK),
                SmokeEndpoint.get("productos-by-id", "/productos/{productId}", SmokeAuth.NONE, OK),
                SmokeEndpoint.get("productos-by-id-lista", "/productos/{productId}?listaPrecio=ECOMMERCE", SmokeAuth.NONE, OK),
                SmokeEndpoint.get("categorias-list", "/categorias", SmokeAuth.NONE, OK),
                SmokeEndpoint.get("categorias-by-id", "/categorias/{categoriaId}", SmokeAuth.NONE, OK),

                // --- Auth ---
                SmokeEndpoint.get("auth-me-staff", "/auth/me", SmokeAuth.STAFF, OK),
                SmokeEndpoint.get("auth-me-cliente", "/auth/me", SmokeAuth.CLIENTE, OK),

                // --- Dashboard / admin ---
                SmokeEndpoint.get("dashboard-kpis", "/dashboard/kpis", SmokeAuth.STAFF, OK),
                SmokeEndpoint.get("admin-buscar", "/admin/buscar?q=Lenovo", SmokeAuth.STAFF, OK),
                SmokeEndpoint.get("admin-notificaciones", "/admin/notificaciones", SmokeAuth.STAFF, OK),

                // --- Billing ---
                SmokeEndpoint.get("presupuestos-list", "/presupuestos", SmokeAuth.STAFF, OK),
                SmokeEndpoint.get("presupuestos-by-id", "/presupuestos/{presupuestoId}", SmokeAuth.STAFF, OK_OR_NOT_FOUND),
                SmokeEndpoint.get("remitos-list", "/remitos", SmokeAuth.STAFF, OK),
                SmokeEndpoint.get("remitos-by-id", "/remitos/{remitoId}", SmokeAuth.STAFF, OK_OR_NOT_FOUND),
                SmokeEndpoint.get("facturas-list", "/facturas", SmokeAuth.STAFF, OK),
                SmokeEndpoint.get("facturas-by-id", "/facturas/{facturaId}", SmokeAuth.STAFF, OK_OR_NOT_FOUND),
                SmokeEndpoint.get("pedidos-list", "/pedidos", SmokeAuth.STAFF, OK),
                SmokeEndpoint.get("pedidos-by-id", "/pedidos/{pedidoId}", SmokeAuth.STAFF, OK_OR_NOT_FOUND),
                SmokeEndpoint.get("pedidos-detalle", "/pedidos/{pedidoId}/detalle", SmokeAuth.STAFF, OK_OR_NOT_FOUND),
                SmokeEndpoint.get("pagos-list", "/pagos", SmokeAuth.STAFF, OK),
                SmokeEndpoint.get("pagos-by-id", "/pagos/{pagoId}", SmokeAuth.STAFF, OK_OR_NOT_FOUND),
                SmokeEndpoint.get("pagos-qr", "/pagos/qr?idPedido=1&monto=89000", SmokeAuth.STAFF, OK),
                SmokeEndpoint.get("ordenes-compra-list", "/ordenes-compra", SmokeAuth.STAFF, OK),
                SmokeEndpoint.get("ordenes-compra-by-id", "/ordenes-compra/{ordenCompraId}", SmokeAuth.STAFF, OK_OR_NOT_FOUND),

                // --- Inventario / catálogo staff ---
                SmokeEndpoint.get("productos-stock-bajo", "/productos/stock-bajo", SmokeAuth.STAFF, OK),
                SmokeEndpoint.get("listas-precios-list", "/listas-precios", SmokeAuth.STAFF, OK),
                SmokeEndpoint.get("listas-precios-by-id", "/listas-precios/{listaPrecioId}", SmokeAuth.STAFF, OK_OR_NOT_FOUND),
                SmokeEndpoint.get("listas-precios-codigo", "/listas-precios/codigo/{listaCodigo}", SmokeAuth.STAFF, OK),
                SmokeEndpoint.get("listas-precios-detalles", "/listas-precios/{listaPrecioId}/detalles", SmokeAuth.STAFF, OK_OR_NOT_FOUND),
                SmokeEndpoint.get("listas-precios-resolver", "/listas-precios/resolver?productoId=1&lista=ECOMMERCE", SmokeAuth.STAFF, OK),
                SmokeEndpoint.get("listas-precios-precio-producto", "/listas-precios/codigo/{listaCodigo}/precio/{productId}", SmokeAuth.STAFF, OK),

                // --- CRM ---
                SmokeEndpoint.get("crm-resumen", "/crm/resumen", SmokeAuth.STAFF, OK),
                SmokeEndpoint.get("crm-conversaciones-resumen", "/crm/conversaciones/resumen", SmokeAuth.STAFF, OK),
                SmokeEndpoint.get("crm-conversaciones-list", "/crm/conversaciones", SmokeAuth.STAFF, OK),
                SmokeEndpoint.get("crm-conversaciones-filter", "/crm/conversaciones?canal=EMAIL&estado=PENDIENTE", SmokeAuth.STAFF, OK),
                SmokeEndpoint.get("crm-conversaciones-by-id", "/crm/conversaciones/{conversacionId}", SmokeAuth.STAFF, OK_OR_NOT_FOUND),
                SmokeEndpoint.get("crm-conversaciones-mensajes", "/crm/conversaciones/{conversacionId}/mensajes", SmokeAuth.STAFF, OK_OR_NOT_FOUND),
                SmokeEndpoint.get("crm-integraciones-list", "/crm/integraciones", SmokeAuth.STAFF, OK),
                SmokeEndpoint.get("interacciones-list", "/interacciones", SmokeAuth.STAFF, OK),
                SmokeEndpoint.get("interacciones-by-cliente", "/interacciones?clienteId=1", SmokeAuth.STAFF, OK),
                SmokeEndpoint.get("interacciones-by-id", "/interacciones/{interaccionId}", SmokeAuth.STAFF, OK_OR_NOT_FOUND),
                SmokeEndpoint.get("campanas-list", "/campanas", SmokeAuth.STAFF, OK),
                SmokeEndpoint.get("campanas-by-id", "/campanas/{campanaId}", SmokeAuth.STAFF, OK_OR_NOT_FOUND),
                SmokeEndpoint.get("campanas-mensajes", "/campanas/{campanaId}/mensajes", SmokeAuth.STAFF, OK_OR_NOT_FOUND),
                SmokeEndpoint.get("promociones-list", "/promociones", SmokeAuth.STAFF, OK),
                SmokeEndpoint.get("promociones-by-id", "/promociones/{promocionId}", SmokeAuth.STAFF, OK_OR_NOT_FOUND),

                // --- Config / contabilidad / RBAC ---
                SmokeEndpoint.get("contabilidad-resumen", "/config/contabilidad/resumen", SmokeAuth.STAFF, OK),
                SmokeEndpoint.get("configuracion-grupo", "/configuracion/{grupo}", SmokeAuth.STAFF, OK),
                SmokeEndpoint.get("configuracion-grupo-mapa", "/configuracion/{grupo}/mapa", SmokeAuth.STAFF, OK),
                SmokeEndpoint.get("configuracion-auditoria", "/configuracion/auditoria/registros", SmokeAuth.STAFF, OK),
                SmokeEndpoint.get("configuracion-logs", "/configuracion/logs/registros", SmokeAuth.STAFF, OK),
                SmokeEndpoint.get("configuracion-auditoria-filter", "/configuracion/auditoria/registros?q=Factura", SmokeAuth.STAFF, OK),
                SmokeEndpoint.get("rbac-matriz", "/configuracion/rbac/matriz", SmokeAuth.STAFF, OK),
                SmokeEndpoint.get("rbac-roles", "/configuracion/rbac/roles", SmokeAuth.STAFF, OK),
                SmokeEndpoint.get("rbac-permisos-usuario", "/configuracion/rbac/permisos-usuario/{rolClave}", SmokeAuth.STAFF, OK_OR_NOT_FOUND),
                SmokeEndpoint.get("config-emisores-list", "/config/emisores", SmokeAuth.STAFF, OK),
                SmokeEndpoint.get("config-emisores-by-id", "/config/emisores/{emisorId}", SmokeAuth.STAFF, OK_OR_NOT_FOUND),
                SmokeEndpoint.get("config-plantillas-list", "/config/plantillas", SmokeAuth.STAFF, OK),
                SmokeEndpoint.get("config-plantillas-preview-id", "/config/plantillas/{plantillaId}/preview", SmokeAuth.STAFF, OK_OR_NOT_FOUND),
                SmokeEndpoint.get("config-plantillas-preview-tipo", "/config/plantillas/preview/{tipoPlantilla}", SmokeAuth.STAFF, OK_OR_NOT_FOUND),
                SmokeEndpoint.get("config-plantillas-render-factura", "/config/plantillas/render/FACTURA/{facturaId}", SmokeAuth.STAFF, OK_OR_NOT_FOUND),
                SmokeEndpoint.get("config-plantillas-render-presupuesto", "/config/plantillas/render/PRESUPUESTO/{presupuestoId}", SmokeAuth.STAFF, OK_OR_NOT_FOUND),
                SmokeEndpoint.get("config-plantillas-render-remito", "/config/plantillas/render/REMITO/{remitoId}", SmokeAuth.STAFF, OK_OR_NOT_FOUND),
                SmokeEndpoint.get("config-catalogos-list", "/config/catalogos", SmokeAuth.STAFF, OK),

                // --- Perfiles / usuarios ---
                SmokeEndpoint.get("usuarios-list", "/usuarios", SmokeAuth.STAFF, OK),
                SmokeEndpoint.get("usuarios-by-id", "/usuarios/{usuarioId}", SmokeAuth.STAFF, OK_OR_NOT_FOUND),
                SmokeEndpoint.get("perfiles-list", "/perfiles", SmokeAuth.STAFF, OK),
                SmokeEndpoint.get("perfiles-by-id", "/perfiles/{perfilId}", SmokeAuth.STAFF, OK_OR_NOT_FOUND),
                SmokeEndpoint.get("perfiles-metricas", "/perfiles/{perfilId}/metricas", SmokeAuth.STAFF, OK_OR_NOT_FOUND),
                SmokeEndpoint.get("perfiles-historial", "/perfiles/{perfilId}/historial", SmokeAuth.STAFF, OK_OR_NOT_FOUND),

                // --- Cuotas / planes ---
                SmokeEndpoint.get("planes-list", "/planes", SmokeAuth.STAFF, OK),
                SmokeEndpoint.get("planes-by-id", "/planes/{planCuotasId}", SmokeAuth.STAFF, OK_OR_NOT_FOUND),
                SmokeEndpoint.get("cuotas-list", "/cuotas", SmokeAuth.STAFF, OK),
                SmokeEndpoint.get("cuotas-vencidas", "/cuotas/vencidas", SmokeAuth.STAFF, OK),
                SmokeEndpoint.get("cuotas-por-vencer", "/cuotas/por-vencer", SmokeAuth.STAFF, OK),
                SmokeEndpoint.get("cuotas-by-plan", "/cuotas/plan/{planCuotasId}", SmokeAuth.STAFF, OK_OR_NOT_FOUND),
                SmokeEndpoint.get("cuotas-by-id", "/cuotas/{cuotaId}", SmokeAuth.STAFF, OK_OR_NOT_FOUND),

                // --- Envíos / carrito / reseñas (authenticated) ---
                SmokeEndpoint.get("envios-list", "/envios", SmokeAuth.STAFF, OK),
                SmokeEndpoint.get("envios-by-id", "/envios/{envioId}", SmokeAuth.STAFF, OK_OR_NOT_FOUND),
                SmokeEndpoint.get("envios-detalle", "/envios/{envioId}/detalle", SmokeAuth.STAFF, OK_OR_NOT_FOUND),
                SmokeEndpoint.get("carritos-list", "/carritos", SmokeAuth.AUTH, OK),
                SmokeEndpoint.get("carritos-by-id", "/carritos/{carritoId}", SmokeAuth.AUTH, OK_OR_NOT_FOUND),
                SmokeEndpoint.get("detalle-carritos-list", "/detalle-carritos", SmokeAuth.AUTH, OK),
                SmokeEndpoint.get("detalle-carritos-by-id", "/detalle-carritos/{detalleCarritoId}", SmokeAuth.AUTH, OK_OR_NOT_FOUND),
                SmokeEndpoint.get("detalle-pedidos-list", "/detalle-pedidos", SmokeAuth.AUTH, OK),
                SmokeEndpoint.get("detalle-pedidos-by-id", "/detalle-pedidos/{detallePedidoId}", SmokeAuth.AUTH, OK_OR_NOT_FOUND),
                SmokeEndpoint.get("resenas-list", "/resenas", SmokeAuth.AUTH, OK),
                SmokeEndpoint.get("resenas-by-id", "/resenas/{resenaId}", SmokeAuth.AUTH, OK_OR_NOT_FOUND),

                // --- Cliente portal ---
                SmokeEndpoint.get("cliente-pedidos", "/cliente/pedidos", SmokeAuth.CLIENTE, OK),
                SmokeEndpoint.get("cliente-pedidos-by-id", "/cliente/pedidos/{pedidoId}", SmokeAuth.CLIENTE, OK_OR_NOT_FOUND),
                SmokeEndpoint.get("cliente-facturas", "/cliente/facturas", SmokeAuth.CLIENTE, OK),
                SmokeEndpoint.get("cliente-facturas-by-id", "/cliente/facturas/{facturaId}", SmokeAuth.CLIENTE, OK_OR_NOT_FOUND),
                SmokeEndpoint.get("cliente-perfil", "/cliente/perfil", SmokeAuth.CLIENTE, OK),
                SmokeEndpoint.get("cliente-tickets", "/cliente/tickets", SmokeAuth.CLIENTE, OK),
                SmokeEndpoint.get("cliente-tickets-mensajes", "/cliente/tickets/{ticketId}/mensajes", SmokeAuth.CLIENTE, OK_OR_NOT_FOUND),
                SmokeEndpoint.get("cliente-devoluciones", "/cliente/devoluciones", SmokeAuth.CLIENTE, OK),
                SmokeEndpoint.get("cliente-devoluciones-by-id", "/cliente/devoluciones/{devolucionId}", SmokeAuth.CLIENTE, OK_OR_NOT_FOUND),
                SmokeEndpoint.get("cliente-cuotas", "/cliente/cuotas", SmokeAuth.CLIENTE, OK),
                SmokeEndpoint.get("cliente-prestamos", "/cliente/prestamos", SmokeAuth.CLIENTE, OK)
        );
    }

    /** Staff-only routes probed without credentials — expect 401. */
    public static List<SmokeEndpoint> staffProtectedSamples() {
        return List.of(
                SmokeEndpoint.get("staff-presupuestos-anon", "/presupuestos", SmokeAuth.NONE, OK_OR_UNAUTHORIZED),
                SmokeEndpoint.get("staff-dashboard-anon", "/dashboard/kpis", SmokeAuth.NONE, OK_OR_UNAUTHORIZED),
                SmokeEndpoint.get("staff-contabilidad-anon", "/config/contabilidad/resumen", SmokeAuth.NONE, OK_OR_UNAUTHORIZED)
        );
    }

    /** Cliente routes probed with staff cookie — expect 403. */
    public static List<SmokeEndpoint> clienteOnlyWithStaff() {
        return List.of(
                SmokeEndpoint.get("cliente-pedidos-staff", "/cliente/pedidos", SmokeAuth.STAFF, OK_OR_FORBIDDEN),
                SmokeEndpoint.get("cliente-perfil-staff", "/cliente/perfil", SmokeAuth.STAFF, OK_OR_FORBIDDEN)
        );
    }

    /** Staff routes probed with cliente cookie — expect 403. */
    public static List<SmokeEndpoint> staffOnlyWithCliente() {
        return List.of(
                SmokeEndpoint.get("presupuestos-cliente", "/presupuestos", SmokeAuth.CLIENTE, OK_OR_FORBIDDEN),
                SmokeEndpoint.get("facturas-cliente", "/facturas", SmokeAuth.CLIENTE, OK_OR_FORBIDDEN),
                SmokeEndpoint.get("contabilidad-cliente", "/config/contabilidad/resumen", SmokeAuth.CLIENTE, OK_OR_FORBIDDEN),
                SmokeEndpoint.get("dashboard-cliente", "/dashboard/kpis", SmokeAuth.CLIENTE, OK_OR_FORBIDDEN)
        );
    }
}
