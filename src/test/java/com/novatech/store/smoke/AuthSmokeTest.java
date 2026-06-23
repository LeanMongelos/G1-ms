package com.novatech.store.smoke;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

class AuthSmokeTest extends SmokeTestBase {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    void staffLoginReturnsUserAndSessionCookie() throws Exception {
        String body = MAPPER.writeValueAsString(
                java.util.Map.of("email", STAFF_EMAIL, "contrasena", STAFF_PASSWORD));
        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andReturn();
        assertEquals(200, result.getResponse().getStatus());
        assertNotNull(result.getResponse().getCookie("novatech_session"));
        JsonNode json = MAPPER.readTree(result.getResponse().getContentAsString());
        assertTrue(json.has("email"));
        assertEquals(STAFF_EMAIL, json.get("email").asText());
    }

    @Test
    void clienteLoginReturnsUserAndSessionCookie() throws Exception {
        String body = MAPPER.writeValueAsString(
                java.util.Map.of("email", CLIENTE_EMAIL, "contrasena", CLIENTE_PASSWORD));
        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andReturn();
        assertEquals(200, result.getResponse().getStatus());
        assertNotNull(result.getResponse().getCookie("novatech_session"));
        JsonNode json = MAPPER.readTree(result.getResponse().getContentAsString());
        assertEquals("CLIENTE", json.get("rol").asText());
    }

    @Test
    void invalidLoginReturns401Not500() throws Exception {
        String body = MAPPER.writeValueAsString(
                java.util.Map.of("email", STAFF_EMAIL, "contrasena", "wrong-password"));
        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andReturn();
        assertEquals(401, result.getResponse().getStatus());
        MAPPER.readTree(result.getResponse().getContentAsString());
    }

    @Test
    void authMeRequiresSession() throws Exception {
        MvcResult anon = mockMvc.perform(get("/auth/me")).andReturn();
        assertEquals(401, anon.getResponse().getStatus());

        MvcResult staff = mockMvc.perform(get("/auth/me").header(HttpHeaders.AUTHORIZATION, "Bearer " + staffToken))
                .andReturn();
        assertEquals(200, staff.getResponse().getStatus());
        MAPPER.readTree(staff.getResponse().getContentAsString());
    }
}
