package com.novatech.store.smoke;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

class ApiSmokeTest extends SmokeTestBase {

    static java.util.stream.Stream<SmokeEndpoint> getEndpoints() {
        return SmokeEndpointCatalog.allGetEndpoints().stream();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("getEndpoints")
    void getEndpointReturnsValidResponse(SmokeEndpoint endpoint) throws Exception {
        String path = ctx.resolvePath(endpoint.path());
        var builder = get(path);
        applyAuth(builder, endpoint.auth());
        MvcResult result = mockMvc.perform(builder).andReturn();
        assertSmokeResponse(result, endpoint);
    }
}
