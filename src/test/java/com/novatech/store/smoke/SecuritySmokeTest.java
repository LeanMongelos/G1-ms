package com.novatech.store.smoke;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

class SecuritySmokeTest extends SmokeTestBase {

    static java.util.stream.Stream<SmokeEndpoint> staffProtected() {
        return SmokeEndpointCatalog.staffProtectedSamples().stream();
    }

    static java.util.stream.Stream<SmokeEndpoint> clienteOnlyWithStaff() {
        return SmokeEndpointCatalog.clienteOnlyWithStaff().stream();
    }

    static java.util.stream.Stream<SmokeEndpoint> staffOnlyWithCliente() {
        return SmokeEndpointCatalog.staffOnlyWithCliente().stream();
    }

    @ParameterizedTest(name = "anon {0}")
    @MethodSource("staffProtected")
    void staffRoutesRejectAnonymous(SmokeEndpoint endpoint) throws Exception {
        MvcResult result = mockMvc.perform(get(endpoint.path())).andReturn();
        assertSmokeResponse(result, endpoint);
    }

    @ParameterizedTest(name = "staff-on-cliente {0}")
    @MethodSource("clienteOnlyWithStaff")
    void clienteRoutesRejectStaff(SmokeEndpoint endpoint) throws Exception {
        var builder = get(endpoint.path());
        applyAuth(builder, SmokeAuth.STAFF);
        MvcResult result = mockMvc.perform(builder).andReturn();
        assertSmokeResponse(result, endpoint);
    }

    @ParameterizedTest(name = "cliente-on-staff {0}")
    @MethodSource("staffOnlyWithCliente")
    void staffRoutesRejectCliente(SmokeEndpoint endpoint) throws Exception {
        var builder = get(endpoint.path());
        applyAuth(builder, SmokeAuth.CLIENTE);
        MvcResult result = mockMvc.perform(builder).andReturn();
        assertSmokeResponse(result, endpoint);
    }
}
