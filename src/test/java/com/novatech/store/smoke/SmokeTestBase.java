package com.novatech.store.smoke;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.novatech.store.security.JwtAuthFilter;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class SmokeTestBase {

    protected static final String STAFF_EMAIL = "superadmin@novatech.com";
    protected static final String STAFF_PASSWORD = "admin123";
    protected static final String CLIENTE_EMAIL = "cliente@novatech.com";
    protected static final String CLIENTE_PASSWORD = "cliente123";

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Autowired
    protected MockMvc mockMvc;

    protected String staffToken;
    protected String clienteToken;
    protected SmokeContext ctx;

    @BeforeEach
    void setUpBase() throws Exception {
        SecurityContextHolder.clearContext();
        staffToken = loginToken(STAFF_EMAIL, STAFF_PASSWORD);
        clienteToken = loginToken(CLIENTE_EMAIL, CLIENTE_PASSWORD);
        ctx = SmokeContext.discover(mockMvc, staffToken, clienteToken);
        SecurityContextHolder.clearContext();
    }

    protected String loginToken(String email, String password) throws Exception {
        String body = MAPPER.writeValueAsString(java.util.Map.of("email", email, "contrasena", password));
        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andReturn();
        assertTrue(result.getResponse().getStatus() == 200,
                "Login failed for " + email + " status=" + result.getResponse().getStatus());
        var cookie = result.getResponse().getCookie(JwtAuthFilter.COOKIE_NAME);
        assertTrue(cookie != null && cookie.getValue() != null && !cookie.getValue().isBlank(),
                "Login cookie missing for " + email);
        return cookie.getValue();
    }

    protected void applyAuth(MockHttpServletRequestBuilder builder, SmokeAuth auth) {
        SecurityContextHolder.clearContext();
        String token = switch (auth) {
            case STAFF -> staffToken;
            case CLIENTE -> clienteToken;
            case AUTH -> staffToken;
            case NONE -> null;
        };
        if (token != null) {
            builder.header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        }
    }

    protected void assertSmokeResponse(MvcResult result, SmokeEndpoint endpoint) throws Exception {
        int status = result.getResponse().getStatus();
        assertFalse(status >= 500,
                endpoint.name() + " " + endpoint.path() + " returned server error " + status
                        + " body=" + result.getResponse().getContentAsString());

        boolean allowed = false;
        for (int code : endpoint.allowedStatuses()) {
            if (status == code) {
                allowed = true;
                break;
            }
        }
        assertTrue(allowed,
                endpoint.name() + " " + endpoint.path() + " status " + status
                        + " not in allowed " + java.util.Arrays.toString(endpoint.allowedStatuses()));

        String contentType = result.getResponse().getContentType();
        if (status == 200 && contentType != null && contentType.contains("application/json")) {
            String responseBody = result.getResponse().getContentAsString();
            assertFalse(responseBody.isBlank(), endpoint.name() + " returned empty JSON body");
            MAPPER.readTree(responseBody);
            assertFalse(responseBody.contains("}}}}"),
                    endpoint.name() + " response looks like circular-ref garbage");
        }
    }
}
