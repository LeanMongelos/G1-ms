package com.novatech.store.smoke;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
