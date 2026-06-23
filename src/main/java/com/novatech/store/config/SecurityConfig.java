package com.novatech.store.config;

import com.novatech.store.security.JwtAuthFilter;
import com.novatech.store.security.SecurityUtils;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private static final String[] STAFF_ONLY = {
            "/usuarios/**",
            "/configuracion/**",
            "/config/**",
            "/crm/**",
            "/facturas/**",
            "/presupuestos/**",
            "/remitos/**",
            "/rbac/**",
            "/admin/**",
            "/dashboard/**",
            "/envios/**",
            "/listas-precios/**",
            "/campanas/**",
            "/promociones/**",
            "/ordenes-compra/**",
            "/cuotas/**",
            "/planes/**",
            "/interacciones/**",
            "/pagos/**",
            "/perfiles/**",
    };

    @Value("${app.cors.allowed-origins:http://localhost:4200,http://127.0.0.1:4200}")
    private String allowedOrigins;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers(headers -> headers
                        .contentTypeOptions(c -> {})
                        .frameOptions(f -> f.deny())
                        .referrerPolicy(r -> r.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                        .httpStrictTransportSecurity(h -> h.includeSubDomains(true).maxAgeInSeconds(31536000))
                        .contentSecurityPolicy(csp -> csp.policyDirectives(
                                "default-src 'self'; frame-ancestors 'none'; object-src 'none'; base-uri 'self'")))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/login", "/auth/register", "/auth/logout").permitAll()
                        .requestMatchers(HttpMethod.GET, "/health", "/health/**", "/actuator/health").permitAll()
                        .requestMatchers("/cliente/**").access((authentication, context) -> {
                            if (authentication.get() == null || !authentication.get().isAuthenticated()) {
                                return new AuthorizationDecision(false);
                            }
                            var principal = authentication.get().getPrincipal();
                            if (principal instanceof com.novatech.store.dto.UsuarioResponse u) {
                                return new AuthorizationDecision(SecurityUtils.esCliente(u.rol()));
                            }
                            return new AuthorizationDecision(false);
                        })
                        .requestMatchers(STAFF_ONLY).access((authentication, context) -> {
                            if (authentication.get() == null || !authentication.get().isAuthenticated()) {
                                return new AuthorizationDecision(false);
                            }
                            var principal = authentication.get().getPrincipal();
                            if (principal instanceof com.novatech.store.dto.UsuarioResponse u) {
                                return new AuthorizationDecision(SecurityUtils.esStaff(u.rol()));
                            }
                            return new AuthorizationDecision(false);
                        })
                        .requestMatchers(HttpMethod.GET, "/productos/stock-bajo").access((authentication, context) -> {
                            if (authentication.get() == null || !authentication.get().isAuthenticated()) {
                                return new AuthorizationDecision(false);
                            }
                            var principal = authentication.get().getPrincipal();
                            if (principal instanceof com.novatech.store.dto.UsuarioResponse u) {
                                return new AuthorizationDecision(SecurityUtils.esStaff(u.rol()));
                            }
                            return new AuthorizationDecision(false);
                        })
                        .requestMatchers(HttpMethod.POST, "/productos", "/productos/**").access((authentication, context) -> {
                            if (authentication.get() == null || !authentication.get().isAuthenticated()) {
                                return new AuthorizationDecision(false);
                            }
                            var principal = authentication.get().getPrincipal();
                            if (principal instanceof com.novatech.store.dto.UsuarioResponse u) {
                                return new AuthorizationDecision(SecurityUtils.esStaff(u.rol()));
                            }
                            return new AuthorizationDecision(false);
                        })
                        .requestMatchers(HttpMethod.PUT, "/productos/**").access((authentication, context) -> {
                            if (authentication.get() == null || !authentication.get().isAuthenticated()) {
                                return new AuthorizationDecision(false);
                            }
                            var principal = authentication.get().getPrincipal();
                            if (principal instanceof com.novatech.store.dto.UsuarioResponse u) {
                                return new AuthorizationDecision(SecurityUtils.esStaff(u.rol()));
                            }
                            return new AuthorizationDecision(false);
                        })
                        .requestMatchers(HttpMethod.DELETE, "/productos/**").access((authentication, context) -> {
                            if (authentication.get() == null || !authentication.get().isAuthenticated()) {
                                return new AuthorizationDecision(false);
                            }
                            var principal = authentication.get().getPrincipal();
                            if (principal instanceof com.novatech.store.dto.UsuarioResponse u) {
                                return new AuthorizationDecision(SecurityUtils.esStaff(u.rol()));
                            }
                            return new AuthorizationDecision(false);
                        })
                        .requestMatchers(HttpMethod.POST, "/categorias", "/categorias/**").access((authentication, context) -> {
                            if (authentication.get() == null || !authentication.get().isAuthenticated()) {
                                return new AuthorizationDecision(false);
                            }
                            var principal = authentication.get().getPrincipal();
                            if (principal instanceof com.novatech.store.dto.UsuarioResponse u) {
                                return new AuthorizationDecision(SecurityUtils.esStaff(u.rol()));
                            }
                            return new AuthorizationDecision(false);
                        })
                        .requestMatchers(HttpMethod.PUT, "/categorias/**").access((authentication, context) -> {
                            if (authentication.get() == null || !authentication.get().isAuthenticated()) {
                                return new AuthorizationDecision(false);
                            }
                            var principal = authentication.get().getPrincipal();
                            if (principal instanceof com.novatech.store.dto.UsuarioResponse u) {
                                return new AuthorizationDecision(SecurityUtils.esStaff(u.rol()));
                            }
                            return new AuthorizationDecision(false);
                        })
                        .requestMatchers(HttpMethod.DELETE, "/categorias/**").access((authentication, context) -> {
                            if (authentication.get() == null || !authentication.get().isAuthenticated()) {
                                return new AuthorizationDecision(false);
                            }
                            var principal = authentication.get().getPrincipal();
                            if (principal instanceof com.novatech.store.dto.UsuarioResponse u) {
                                return new AuthorizationDecision(SecurityUtils.esStaff(u.rol()));
                            }
                            return new AuthorizationDecision(false);
                        })
                        .requestMatchers(HttpMethod.GET, "/productos", "/productos/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/categorias", "/categorias/**").permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"message\":\"Sesión requerida\"}");
                        }))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        List<String> origins = Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
        config.setAllowedOriginPatterns(origins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
