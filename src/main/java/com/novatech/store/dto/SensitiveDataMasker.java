package com.novatech.store.dto;

import java.util.regex.Pattern;

public final class SensitiveDataMasker {

    private static final Pattern SENSITIVE = Pattern.compile(
            "(\"(?:token|password|secret|api[_-]?key|access[_-]?token|client[_-]?secret|refresh[_-]?token|webhook[_-]?secret)\"\\s*:\\s*\")([^\"\\\\]*)(\")",
            Pattern.CASE_INSENSITIVE);

    private SensitiveDataMasker() {
    }

    public static String enmascararJson(String json) {
        if (json == null || json.isBlank()) {
            return json;
        }
        return SENSITIVE.matcher(json).replaceAll("$1***$3");
    }
}
