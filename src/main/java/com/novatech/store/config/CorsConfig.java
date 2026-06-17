package com.novatech.store.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// CORS es un permiso de seguridad de los navegadores.
// Por defecto, una pagina web (como nuestro Angular en el puerto 4200) NO puede
// llamar a un servidor en otro puerto (nuestro backend en el 8080).
// Aca le decimos al backend que SI acepte los pedidos que vengan del frontend.
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // se aplica a todas las rutas del backend
                .allowedOrigins("http://localhost:4200") // permitimos que llame nuestro Angular
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS") // metodos permitidos
                .allowedHeaders("*"); // permitimos cualquier encabezado
    }
}
