package com.novatech.store.smoke;

import org.springframework.http.HttpMethod;

/**
 * Declarative smoke target. Paths may contain placeholders resolved by {@link SmokeContext}
 * (e.g. {@code {productId}}, {@code {pedidoId}}).
 */
public record SmokeEndpoint(
        String name,
        HttpMethod method,
        String path,
        SmokeAuth auth,
        int[] allowedStatuses) {

    public SmokeEndpoint {
        if (allowedStatuses == null || allowedStatuses.length == 0) {
            allowedStatuses = new int[] {200};
        }
    }

    public static SmokeEndpoint get(String name, String path, SmokeAuth auth, int... allowedStatuses) {
        return new SmokeEndpoint(name, HttpMethod.GET, path, auth, allowedStatuses);
    }
}
