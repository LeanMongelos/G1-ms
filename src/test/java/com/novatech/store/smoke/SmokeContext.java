package com.novatech.store.smoke;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

/** Resolves path placeholders and caches IDs discovered from list endpoints. */
public final class SmokeContext {

    private static final Pattern PLACEHOLDER = Pattern.compile("\\{([a-zA-Z0-9_]+)}");
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final Map<String, String> values = new HashMap<>();

    private SmokeContext() {
        values.put("productId", "1");
        values.put("categoriaId", "1");
        values.put("pedidoId", "1");
        values.put("carritoId", "1");
        values.put("detalleCarritoId", "1");
        values.put("detallePedidoId", "1");
        values.put("pagoId", "1");
        values.put("envioId", "1");
        values.put("resenaId", "1");
        values.put("planId", "1");
        values.put("cuotaId", "1");
        values.put("perfilId", "1");
        values.put("usuarioId", "99");
        values.put("clienteUsuarioId", "2");
        values.put("listaCodigo", "ECOMMERCE");
        values.put("grupo", "contabilidad");
        values.put("tipoPlantilla", "FACTURA");
        values.put("rolClave", "SUPERADMIN");
    }

    public static SmokeContext discover(MockMvc mockMvc, String staffToken, String clienteToken) throws Exception {
        SmokeContext ctx = new SmokeContext();
        ctx.discoverFromList(mockMvc, staffToken, "/presupuestos", "presupuestoId", "idPresupuesto");
        ctx.discoverFromList(mockMvc, staffToken, "/remitos", "remitoId", "idRemito");
        ctx.discoverFromList(mockMvc, staffToken, "/facturas", "facturaId", "idFactura");
        ctx.discoverFromList(mockMvc, staffToken, "/usuarios", "usuarioListId", "idUsuario");
        ctx.discoverFromList(mockMvc, staffToken, "/config/plantillas", "plantillaId", "idPlantilla");
        ctx.discoverFromList(mockMvc, staffToken, "/config/emisores", "emisorId", "idEmisor");
        ctx.discoverFromList(mockMvc, staffToken, "/listas-precios", "listaPrecioId", "idListaPrecio");
        ctx.discoverFromList(mockMvc, staffToken, "/crm/conversaciones", "conversacionId", "idConversacion");
        ctx.discoverFromList(mockMvc, staffToken, "/campanas", "campanaId", "idCampana");
        ctx.discoverFromList(mockMvc, staffToken, "/promociones", "promocionId", "idPromocion");
        ctx.discoverFromList(mockMvc, staffToken, "/ordenes-compra", "ordenCompraId", "idOrdenCompra");
        ctx.discoverFromList(mockMvc, staffToken, "/interacciones", "interaccionId", "idInteraccion");
        ctx.discoverFromList(mockMvc, staffToken, "/crm/integraciones", "integracionId", "idIntegracion");
        ctx.discoverFromList(mockMvc, staffToken, "/config/catalogos", "catalogoId", "idCatalogo");
        ctx.discoverFromList(mockMvc, staffToken, "/planes", "planCuotasId", "idPlan");
        ctx.discoverFromList(mockMvc, staffToken, "/perfiles", "perfilListId", "idCliente");
        ctx.discoverFromList(mockMvc, staffToken, "/configuracion/rbac/roles", "rolClave", "clave");
        ctx.discoverFromList(mockMvc, staffToken, "/listas-precios/1/detalles", "listaDetalleId", "idDetalle");
        ctx.discoverFromList(mockMvc, clienteToken, "/cliente/tickets", "ticketId", "idConversacion");
        ctx.discoverFromList(mockMvc, clienteToken, "/cliente/devoluciones", "devolucionId", "idDevolucion");
        return ctx;
    }

    public String resolvePath(String template) {
        Matcher matcher = PLACEHOLDER.matcher(template);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String key = matcher.group(1);
            String replacement = values.getOrDefault(key, "1");
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public String get(String key) {
        return values.get(key);
    }

    private void discoverFromList(MockMvc mockMvc, String token, String path, String key, String jsonField)
            throws Exception {
        if (token == null) {
            return;
        }
        MockHttpServletRequestBuilder req = org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get(path)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        MvcResult result = mockMvc.perform(req).andReturn();
        if (result.getResponse().getStatus() != 200) {
            return;
        }
        String body = result.getResponse().getContentAsString();
        if (body == null || body.isBlank()) {
            return;
        }
        JsonNode root = MAPPER.readTree(body);
        JsonNode first = null;
        if (root.isArray() && root.size() > 0) {
            first = root.get(0);
        } else if (root.isObject() && root.has("content") && root.get("content").isArray()
                && root.get("content").size() > 0) {
            first = root.get("content").get(0);
        }
        if (first != null && first.has(jsonField) && !first.get(jsonField).isNull()) {
            values.put(key, first.get(jsonField).asText());
        }
    }
}
