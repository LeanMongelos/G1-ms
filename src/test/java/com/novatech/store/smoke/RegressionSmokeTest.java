package com.novatech.store.smoke;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/** Targeted regression tests for bugs that previously reached production. */
class RegressionSmokeTest extends SmokeTestBase {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    void presupuestosListReturnsJsonArrayWithoutServerError() throws Exception {
        var builder = get("/presupuestos");
        applyAuth(builder, SmokeAuth.STAFF);
        MvcResult result = mockMvc.perform(builder).andReturn();
        assertEquals(200, result.getResponse().getStatus());
        JsonNode json = MAPPER.readTree(result.getResponse().getContentAsString());
        assertTrue(json.isArray(), "presupuestos must return JSON array");
    }

    @Test
    void remitosListReturnsJsonArrayWithoutServerError() throws Exception {
        var builder = get("/remitos");
        applyAuth(builder, SmokeAuth.STAFF);
        MvcResult result = mockMvc.perform(builder).andReturn();
        assertEquals(200, result.getResponse().getStatus());
        JsonNode json = MAPPER.readTree(result.getResponse().getContentAsString());
        assertTrue(json.isArray(), "remitos must return JSON array");
    }

    @Test
    void facturasListReturnsParseableJsonWithoutHugePayload() throws Exception {
        var builder = get("/facturas");
        applyAuth(builder, SmokeAuth.STAFF);
        MvcResult result = mockMvc.perform(builder).andReturn();
        assertEquals(200, result.getResponse().getStatus());
        String body = result.getResponse().getContentAsString();
        JsonNode json = MAPPER.readTree(body);
        assertTrue(json.isArray(), "facturas must return JSON array");
        assertTrue(body.length() < 2_000_000, "facturas response suspiciously large");
    }

    @Test
    void clientePedidosAndPerfilReturnJsonForCliente() throws Exception {
        var pedidos = get("/cliente/pedidos");
        applyAuth(pedidos, SmokeAuth.CLIENTE);
        MvcResult pedidosResult = mockMvc.perform(pedidos).andReturn();
        assertEquals(200, pedidosResult.getResponse().getStatus());
        assertTrue(MAPPER.readTree(pedidosResult.getResponse().getContentAsString()).isArray());

        var perfil = get("/cliente/perfil");
        applyAuth(perfil, SmokeAuth.CLIENTE);
        MvcResult perfilResult = mockMvc.perform(perfil).andReturn();
        assertEquals(200, perfilResult.getResponse().getStatus());
        assertTrue(MAPPER.readTree(perfilResult.getResponse().getContentAsString()).isObject());
    }

    @Test
    void deleteProductoReferenciadoReturns400Not500() throws Exception {
        var builder = delete("/productos/1");
        applyAuth(builder, SmokeAuth.STAFF);
        MvcResult result = mockMvc.perform(builder).andReturn();
        int status = result.getResponse().getStatus();
        assertTrue(status == 400 || status == 409,
                "referenced product delete should return 400/409, got " + status);
        assertTrue(result.getResponse().getContentType().contains("application/json"));
    }

    @Test
    void enviosListIncludesPedidoForTableDisplay() throws Exception {
        var builder = get("/envios");
        applyAuth(builder, SmokeAuth.STAFF);
        MvcResult result = mockMvc.perform(builder).andReturn();
        assertEquals(200, result.getResponse().getStatus());
        JsonNode json = MAPPER.readTree(result.getResponse().getContentAsString());
        assertTrue(json.isArray(), "envios must return JSON array");
        if (json.size() > 0) {
            JsonNode first = json.get(0);
            assertTrue(first.has("pedido"), "envio list must serialize pedido for admin table");
            assertTrue(first.get("pedido").has("idPedido"), "pedido must include idPedido");
        }
    }

    @Test
    void contabilidadResumenReturnsJsonObjectWithoutRecursion() throws Exception {
        var builder = get("/config/contabilidad/resumen");
        applyAuth(builder, SmokeAuth.STAFF);
        MvcResult result = mockMvc.perform(builder).andReturn();
        assertEquals(200, result.getResponse().getStatus());
        String body = result.getResponse().getContentAsString();
        JsonNode json = MAPPER.readTree(body);
        assertTrue(json.isObject(), "contabilidad resumen must return JSON object");
        assertTrue(json.has("alicuotas") || json.has("config") || json.has("contadores"),
                "contabilidad resumen should include expected keys");
        assertTrue(body.length() < 500_000, "response suspiciously large — possible recursion");
    }
}
