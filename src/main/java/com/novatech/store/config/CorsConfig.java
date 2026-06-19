package com.novatech.store.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// CORS es un permiso de seguridad de los navegadores.
// Por defecto, una pagina web (como nuestro Angular en el puerto 4200) NO puede
// llamar a un servidor en otro puerto (nuestro backend en el 8080).
// Aca le decimos al backend que SI acepte los pedidos que vengan del frontend.
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    // Leemos los origenes permitidos desde application.properties (propiedad app.cors.allowed-origins).
    // Asi en deploy se puede cambiar sin tocar el codigo (solo una variable de entorno).
    // Si vienen varios separados por coma, los partimos en una lista.
    // Por defecto "*" -> acepta cualquier origen. Esto es comodo cuando compartimos
    // la app por un tunel publico (Cloudflare, etc.), porque la URL del tunel cambia
    // y no la queremos andar agregando a mano. En un deploy real conviene poner aca
    // la URL exacta del frontend.
    @Value("${app.cors.allowed-origins:*}")
    private String[] allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Usamos allowedOriginPatterns (en vez de allowedOrigins) porque soporta
        // comodines como "*" o "https://*.trycloudflare.com". Asi el navegador puede
        // llamar al backend aunque entre por el dominio del tunel.
        registry.addMapping("/**") // se aplica a todas las rutas del backend
                .allowedOriginPatterns(allowedOrigins) // los origenes (frontend) que permitimos
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS") // metodos permitidos
                .allowedHeaders("*"); // permitimos cualquier encabezado
    }
}
